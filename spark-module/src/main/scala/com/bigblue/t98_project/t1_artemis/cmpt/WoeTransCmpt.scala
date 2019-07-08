package com.bigblue.t98_project.t1_artemis.cmpt

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.collection.mutable.{ArrayBuffer, LinkedHashMap, WrappedArray}
import scala.util.control.Breaks._

/**
  * Created By TheBigBlue on 2018/10/26 : 17:17
  * Description : WOE转换组件
  */
object WoeTransCmpt {

  /**
    * WOE转换组件--配置名称
    */
  //需要转换的列
  val TRANS_COLS: String = "transCols"
  //字段类型--连续型
  val COL_TYPE_CON: String = "0"
  //字段类型--离散型
  val COL_TYPE_DIS: String = "1"

  def apply(map: LinkedHashMap[String, (String, Array[(String, Double)])], row: Row): LinkedHashMap[String, (String, Array[(String, Double)])] = {
    val colName: String = row.getAs[String]("col_name")
    val colType: String = row.getAs[String]("col_type")
    val boundNumArr: Array[String] = row.getAs[WrappedArray[String]]("bound_num_arr").toArray
    val rateArr: Array[Double] = row.getAs[WrappedArray[Double]]("rate_arr").toArray
    val tuples: Array[(String, Double)] = boundNumArr.zip(rateArr)
    map.put(colName, (colType, tuples))
    map
  }

  def merge(map1: LinkedHashMap[String, (String, Array[(String, Double)])],
            map2: LinkedHashMap[String, (String, Array[(String, Double)])]): LinkedHashMap[String, (String, Array[(String, Double)])] = {
    map1 ++= map2
  }

  /**
    * Created By TheBigBlue On 2018/12/11
    * Description : 接收dataframe、Array，用treeAggregate计算
    */
  def woeTransform1(spark: SparkSession, inputDF: DataFrame, dictDF: DataFrame, woeTransConfigMap: Map[String, Any]): DataFrame = {
    val transCols: Array[String] = woeTransConfigMap.getOrElse(TRANS_COLS, null).asInstanceOf[Array[String]]
    import spark.implicits._
    //拼接批量udf表达式的数组
    val disColExprArr = new ArrayBuffer[String]
    val conColExprArr = new ArrayBuffer[String]
    disColExprArr += "*"
    conColExprArr += "*"
    val disColArr = new ArrayBuffer[String]
    val conColArr = new ArrayBuffer[String]
    //多行转一行
    val aggDF: DataFrame = dictDF.groupBy("col_name", "col_type")
      .agg(collect_list($"bound_num").as("bound_num_arr"), collect_list($"rate").as("rate_arr"))
    val aggMap: LinkedHashMap[String, (String, Array[(String, Double)])] = aggDF.rdd.treeAggregate(new LinkedHashMap[String, (String, Array[(String, Double)])])(apply, merge)
    aggMap.foreach(row => {
      val (colName, colType) = (row._1, row._2._1)
      if (transCols != null && transCols.size > 0) {
        //只转换用户选择的列
        if (transCols.contains(colName)) {
          //拼接批量udf表达式的数组
          if (colType == COL_TYPE_DIS) {
            //离散变量
            disColExprArr += "transDisCol(`" + colName + "`, '" + colName + "')"
            disColArr += colName
          } else {
            //连续变量
            conColExprArr += "transConCol(`" + colName + "`, '" + colName + "')"
            conColArr += colName
          }
        }
      } else {
        //用户未选，对字典中出现的字段全部转换
        if (colType == COL_TYPE_DIS) {
          //离散变量
          disColExprArr += "transDisCol(`" + colName + "`, '" + colName + "')"
          disColArr += colName
        } else {
          //连续变量
          conColExprArr += "transConCol(`" + colName + "`, '" + colName + "')"
          conColArr += colName
        }
      }
    })
    //注册自定义函数，转换连续变量
    spark.udf.register("transConCol", (x: Double, colName: String) => {
      val boundStrArr: Array[(String, Double)] = aggMap.get(colName).get._2
      val boundArr: Array[(Double, Double)] = boundStrArr.map(x => (x._1.toDouble, x._2)).sortWith(_._1 < _._1)
      var transValue: Double = boundArr(0)._2
      for (i <- 0.to(boundArr.length - 1)) {
        breakable {
          if (i == 0) {
            if (x <= boundArr(0)._1) {
              transValue = boundArr(0)._2
              break
            }
          } else if (x > boundArr(i - 1)._1 && x <= boundArr(i)._1) {
            transValue = boundArr(i)._2
            break
          }
        }
      }
      transValue
    })
    //注册自定义函数，转换离散变量
    spark.udf.register("transDisCol", (x: String, colName: String) => {
      val boundStrArr: Array[(String, Double)] = aggMap.get(colName).get._2
      val maybeTuple: Option[(String, Double)] = boundStrArr.find(tuple => tuple._1 == x)
      //离散型没有匹配到的，给0处理
      if (maybeTuple == None) 0.00 else maybeTuple.get._2
    })
    //转化，调用udf，删除之前原列
    var transDF = inputDF.selectExpr(disColExprArr.toArray: _*).drop(disColArr: _*)
    transDF = transDF.selectExpr(conColExprArr.toArray: _*).drop(conColArr: _*)
    //转化后列改为原列名
    val colNames: Array[String] = transDF.schema.fieldNames.map(col => {
      if (col.contains("UDF:")) {
        col.substring(col.lastIndexOf(",") + 2, col.lastIndexOf(")"))
      } else {
        col
      }
    })
    transDF.toDF(colNames: _*)
  }

  /**
    * Created By TheBigBlue On 2018/12/11
    * Description : 接收Dataset、List，用treeAggregate计算，推荐
    */
  def woeTransform2(spark: SparkSession, inputDF: DataFrame, dictDF: Dataset[Row], woeTransConfigMap: Map[String, Any]): DataFrame = {
    val transColsList: List[String] = woeTransConfigMap.getOrElse(TRANS_COLS, null).asInstanceOf[List[String]]
    if (transColsList == null || transColsList.size == 0) throw new Exception("转换列为空！")
    val transCols: Array[String] = transColsList.toArray
    import spark.implicits._
    //拼接批量udf表达式的数组
    val disColExprArr = new ArrayBuffer[String]
    val conColExprArr = new ArrayBuffer[String]
    disColExprArr += "*"
    conColExprArr += "*"
    val disColArr = new ArrayBuffer[String]
    val conColArr = new ArrayBuffer[String]
    //多行转一行
    val aggDF: DataFrame = dictDF.groupBy("col_name", "col_type")
      .agg(collect_list($"bound_num").as("bound_num_arr"), collect_list($"rate").as("rate_arr"))
    val aggMap: LinkedHashMap[String, (String, Array[(String, Double)])] = aggDF.rdd.treeAggregate(new LinkedHashMap[String, (String, Array[(String, Double)])])(apply, merge)
    aggMap.foreach(row => {
      val (colName, colType) = (row._1, row._2._1)
      if (transCols != null && transCols.size > 0) {
        //只转换用户选择的列
        if (transCols.contains(colName)) {
          //拼接批量udf表达式的数组
          if (colType == COL_TYPE_DIS) {
            //离散变量
            disColExprArr += "transDisCol(`" + colName + "`, '" + colName + "')"
            disColArr += colName
          } else {
            //连续变量
            conColExprArr += "transConCol(`" + colName + "`, '" + colName + "')"
            conColArr += colName
          }
        }
      } else {
        //用户未选，对字典中出现的字段全部转换
        if (colType == COL_TYPE_DIS) {
          //离散变量
          disColExprArr += "transDisCol(`" + colName + "`, '" + colName + "')"
          disColArr += colName
        } else {
          //连续变量
          conColExprArr += "transConCol(`" + colName + "`, '" + colName + "')"
          conColArr += colName
        }
      }
    })
    //注册自定义函数，转换连续变量
    spark.udf.register("transConCol", (x: Double, colName: String) => {
      val boundStrArr: Array[(String, Double)] = aggMap.get(colName).get._2
      val boundArr: Array[(Double, Double)] = boundStrArr.map(x => (x._1.toDouble, x._2)).sortWith(_._1 < _._1)
      var transValue: Double = boundArr(0)._2
      for (i <- 0.to(boundArr.length - 1)) {
        breakable {
          if (i == 0) {
            if (x <= boundArr(0)._1) {
              transValue = boundArr(0)._2
              break
            }
          } else if (x > boundArr(i - 1)._1 && x <= boundArr(i)._1) {
            transValue = boundArr(i)._2
            break
          }
        }
      }
      transValue
    })
    //注册自定义函数，转换离散变量
    spark.udf.register("transDisCol", (x: String, colName: String) => {
      val boundStrArr: Array[(String, Double)] = aggMap.get(colName).get._2
      val maybeTuple: Option[(String, Double)] = boundStrArr.find(tuple => tuple._1 == x)
      //离散型没有匹配到的，给0处理
      if (maybeTuple == None) 0.00 else maybeTuple.get._2
    })
    //转化，调用udf，删除之前原列
    var transDF = inputDF.selectExpr(disColExprArr.toArray: _*).drop(disColArr: _*)
    transDF = transDF.selectExpr(conColExprArr.toArray: _*).drop(conColArr: _*)
    //转化后列改为原列名
    val colNames: Array[String] = transDF.schema.fieldNames.map(col => {
      if (col.contains("UDF:")) {
        col.substring(col.lastIndexOf(",") + 2, col.lastIndexOf(")"))
      } else {
        col
      }
    })
    transDF.toDF(colNames: _*)
  }

}
