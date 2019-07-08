package com.bigblue.t98_project.t3_hestia

import com.bigblue.t97_utils.NullValueCheck
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{OneHotEncoderEstimator, StringIndexer}
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, Row, SparkSession}

import scala.collection.mutable.{ArrayBuffer, LinkedHashMap}

/**
  * Created By TheBigBlue on 2019/1/24
  * Description : 
  */
object OneHotTest {

  def main(args: Array[String]): Unit = {
    val warehouseLocate = "hdfs://hcluster/user/hive/warehouse"
    //获取sparkSession
    val spark = SparkSession.builder().master("local").appName("HestiaTest")
      .config("spark.sql.warehouse.dir", warehouseLocate)
      .enableHiveSupport()
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    //输入数据
    //    val inputDF = spark.createDataFrame(Seq(
    //      (0, "log", "555", 111),
    //      (1, "text", "555", 111),
    //      (2, "text", "111", 222),
    //      (3, "大数据", "222", 333),
    //      (4, "text", "333", 333),
    //      (5, "log", "333", 333),
    //      (6, "log", "333", 444),
    //      (7, "log", "444", 444),
    //      (8, "hadoop", "测试", 444)
    //      //      (9, null, "444", 444)
    //    )).toDF("id", "text", "num_txt", "num")
    //    //用户选择的需要转换的离散列
    //    val userSelectCols = Array("text", "num_txt", "num")
    //      val maxCategories = 5


    val maxCategories = 50
    val inputDF = spark.table("holdbase.feature_test4")
    val userSelectCols = Array("产品名称", "申请期限", "申请还款方式", "性别", "学历", "住房情况", "婚姻状况",
      "职务类型", "单位性质", "是否填写配偶信息", "联系人与申请人关系", "申请人星座", "比对最低收入", "住房公积金缴费状态",
      "销户贷记卡最长持续逾期月数", "销户贷记卡最短持续逾期月数", "当前养老保险缴费状态", "曾有呆账", "曾有资产处置", "曾有保证人代偿")



    //    test1(spark, inputDF, userSelectCols)
    //    test2(spark, inputDF, userSelectCols)
    //        test3(spark, inputDF, userSelectCols, maxCategories)
    //        test4(spark, inputDF, userSelectCols, maxCategories)
    test5(spark, inputDF, userSelectCols, maxCategories)
    //    test6(spark, inputDF, userSelectCols, maxCategories)
    //    test7(spark, inputDF, userSelectCols, maxCategories)
  }

  /**
    * @Author: TheBigBlue
    * @Description: 对离散列编码
    * @Date: 2019/1/24
    * @Param:
    * @return:
    **/
  def encode(inputDF: DataFrame, userSelectCols: Array[String]): (DataFrame, Array[String]) = {
    //做转换
    val start = System.currentTimeMillis()
    val inputCols = new ArrayBuffer[String]()
    //使用pipeline一次转换
    val indexers = userSelectCols.map(colName => {
      val transColName = colName + "_indexed"
      inputCols += transColName
      new StringIndexer().setInputCol(colName).setOutputCol(transColName)
    })
    val labelEncodeDF = new Pipeline().setStages(indexers).fit(inputDF).transform(inputDF)
    val end = System.currentTimeMillis()
    println("trans time : " + (end - start))
    (labelEncodeDF, inputCols.toArray)
  }

  /**
    * @Author: TheBigBlue
    * @Description: 测试one-hot，使用approx_count_distinct控制分类个数限制,使用withColumn扩展列，性能最差
    * @Date: 2019/1/24
    * @param spark :
    * @Return:
    **/
  def test3(spark: SparkSession, inputDF: DataFrame, userSelectCols: Array[String], maxCategories: Int): Unit = {
    val startTime = System.currentTimeMillis()
    inputDF.cache()
    //校验空值
    val count = NullValueCheck.countNullValue(inputDF, userSelectCols)
    if (count > 0) {
      println("数据存在" + count + "条空值！")
      return
    }

    //多于用户设置的个数上限，则不编码
    val aggMap = userSelectCols.map((_ -> "approx_count_distinct")).toMap
    val aggRows = inputDF.agg(aggMap).take(1)

    //各列的计数
    val colsCountTuple: Array[(String, Long)] = userSelectCols.map(col => (col, aggRows(0).getAs[Long]("approx_count_distinct(" + col + ")")))
    //不参与编码的列
    val colsNotEncoded = colsCountTuple.filter(_._2 > maxCategories)
    println("不参与编码的列有：" + colsNotEncoded.mkString(","))
    import spark.implicits._
    //不参与编码的列的dataset
    val noEncodedDF = spark.createDataFrame(colsNotEncoded)
      .toDF("col_name", "distinct_count")

    //参与编码的列
    val transCols = colsCountTuple.filter(_._2 <= maxCategories).map(_._1)
    println("参与编码的列有：" + transCols.mkString(","))

    //离散列编码
    val (labelEncodeDF, inputCols) = encode(inputDF, transCols)

    //one-hot编码
    val estimator = new OneHotEncoderEstimator().setInputCols(inputCols).setOutputCols(inputCols.map(_ + "_encoded")).setDropLast(false)
    var oneHotDF = estimator.fit(labelEncodeDF).transform(labelEncodeDF).drop(inputCols: _*).cache()

    //获取各列不同值的统计数据
    val colValueCountMap = oneHotDF.rdd.treeAggregate(new LinkedHashMap[String, LinkedHashMap[String, Long]])(apply, merge)
    transCols.foreach(colName => {
      //vector列名
      val vectorColName = colName + "_indexed_encoded"
      //将vector转为arr的udf
      val vecToArray = udf((xs: SparseVector) => xs.toArray)
      //将该列vector转换为array
      oneHotDF = oneHotDF.withColumn(vectorColName, vecToArray($"$vectorColName"))

      //获取每列的扩展字段名数组
      val newColNameArr = colValueCountMap.get(colName).get.toArray.sortBy(_._2).reverse.map(colName + "_if_" + _._1)
      //扩展字段，删除向量列
      newColNameArr.zipWithIndex.foreach { case (newName, index) => {
        oneHotDF = oneHotDF.withColumn(newName, $"$vectorColName".getItem(index))
      }
      }
      oneHotDF = oneHotDF.drop(vectorColName)
    })
    oneHotDF.unpersist()
    inputDF.unpersist()
    oneHotDF.show()
    println("test3 time : " + (System.currentTimeMillis() - startTime))
  }

  /**
    * @Author: TheBigBlue
    * @Description: 测试one-hot，使用approx_count_distinct控制分类个数限制,使用select扩展列，性能和test3差不多，比3快一点
    * @Date: 2019/1/24
    * @param spark :
    * @Return:
    **/
  def test4(spark: SparkSession, inputDF: DataFrame, userSelectCols: Array[String], maxCategories: Int): Unit = {
    val startTime = System.currentTimeMillis()
    inputDF.cache()
    //校验空值
    val count = NullValueCheck.countNullValue(inputDF, userSelectCols)
    if (count > 0) {
      println("数据存在" + count + "条空值！")
      return
    }

    //多于用户设置的个数上限，则不编码
    val aggMap = userSelectCols.map((_ -> "approx_count_distinct")).toMap
    val aggRows = inputDF.agg(aggMap).take(1)

    //各列的计数
    val colsCountTuple: Array[(String, Long)] = userSelectCols.map(col => (col, aggRows(0).getAs[Long]("approx_count_distinct(" + col + ")")))
    //不参与编码的列
    val colsNotEncoded = colsCountTuple.filter(_._2 > maxCategories)
    println("不参与编码的列有：" + colsNotEncoded.mkString(","))
    import spark.implicits._
    //不参与编码的列的dataset
    val noEncodedDF = spark.createDataFrame(colsNotEncoded)
      .toDF("col_name", "distinct_count")

    //参与编码的列
    val transCols = colsCountTuple.filter(_._2 <= maxCategories).map(_._1)
    println("参与编码的列有：" + transCols.mkString(","))

    //离散列编码
    val (labelEncodeDF, inputCols) = encode(inputDF, transCols)

    //one-hot编码
    val estimator = new OneHotEncoderEstimator().setInputCols(inputCols).setOutputCols(inputCols.map(_ + "_encoded")).setDropLast(false)
    var oneHotDF = estimator.fit(labelEncodeDF).transform(labelEncodeDF).drop(inputCols: _*).cache()

    //获取各列不同值的统计数据
    val colValueCountMap = oneHotDF.rdd.treeAggregate(new LinkedHashMap[String, LinkedHashMap[String, Long]])(apply, merge)
    transCols.foreach(colName => {
      val orginalCols = oneHotDF.schema.fieldNames.map(colName => $"$colName")
      //vector列名
      val vectorColName = colName + "_indexed_encoded"
      //将vector转为arr的udf
      val vecToArray = udf((xs: SparseVector) => xs.toArray)
      //将该列vector转换为array
      oneHotDF = oneHotDF.withColumn(vectorColName, vecToArray($"$vectorColName"))

      //获取每列的扩展字段名数组
      val newColNameArr = colValueCountMap.get(colName).get.toArray.sortBy(_._2).reverse.map(colName + "_if_" + _._1)
      val exprs: Array[Column] = newColNameArr.zipWithIndex.map { case (newColName, index) => {
        $"$vectorColName".getItem(index).alias(newColName)
      }
      }
      oneHotDF = oneHotDF.select((orginalCols ++ exprs): _*).drop(vectorColName)
    })
    oneHotDF.unpersist()
    inputDF.unpersist()
    oneHotDF.show()
    println("test4 time : " + (System.currentTimeMillis() - startTime))
  }

  /**
    * @Author: TheBigBlue
    * @Description: 测试one-hot，使用approx_count_distinct控制分类个数限制,全部使用selectExpr扩展列，性能最好，速度明显快很多
    * @Date: 2019/1/24
    * @param spark :
    * @Return:
    **/
  def test5(spark: SparkSession, inputDF: DataFrame, userSelectCols: Array[String], maxCategories: Int): Unit = {
    val startTime = System.currentTimeMillis()
    inputDF.cache()
    //校验空值
    val count = NullValueCheck.countNullValue(inputDF, userSelectCols)
    if (count > 0) {
      println("数据存在" + count + "条空值！")
      return
    }

    //多于用户设置的个数上限，则不编码
    val aggMap = userSelectCols.map((_ -> "approx_count_distinct")).toMap
    val aggRows = inputDF.agg(aggMap).take(1)

    //各列的计数
    val colsCountTuple: Array[(String, Long)] = userSelectCols.map(col => (col, aggRows(0).getAs[Long]("approx_count_distinct(" + col + ")")))
    //不参与编码的列
    val colsNotEncoded = colsCountTuple.filter(_._2 > maxCategories)
    println("不参与编码的列有：" + colsNotEncoded.mkString(","))
    import spark.implicits._
    //不参与编码的列的dataset
    val noEncodedDF = spark.createDataFrame(colsNotEncoded)
      .toDF("col_name", "distinct_count")

    //参与编码的列
    val transCols = colsCountTuple.filter(_._2 <= maxCategories).map(_._1)
    println("参与编码的列有：" + transCols.mkString(","))

    //离散列编码
    val (labelEncodeDF, inputCols) = encode(inputDF, transCols)

    //one-hot编码
    val estimator = new OneHotEncoderEstimator().setInputCols(inputCols).setOutputCols(inputCols.map(_ + "_encoded")).setDropLast(false)
    val oneHotDF = estimator.fit(labelEncodeDF).transform(labelEncodeDF).drop(inputCols: _*).cache()

    //获取各列不同值的统计数据
    val colValueCountMap = oneHotDF.rdd.treeAggregate(new LinkedHashMap[String, LinkedHashMap[String, Long]])(apply, merge)
    //将vector转为arr的udf
    val vecToArray = udf((xs: SparseVector) => xs.toArray)
    //一次性转换所有向量列
    val vecToArrExprs: Array[Column] = transCols.map(colName => {
      //vector列名
      val vectorColName = colName + "_indexed_encoded"
      vecToArray($"$vectorColName").alias(colName + "_arr_tmp")
    })
    val orginalCols = inputDF.schema.fieldNames.map(colName => $"$colName")
    //将vector转为arr后的dataframe
    val transArrDF = oneHotDF.select((orginalCols ++ vecToArrExprs): _*)

    //一次性扩展所有arr列
    val extendExprs: Array[Column] = transCols.flatMap(colName => {
      //arr列名
      val arrColName = colName + "_arr_tmp"
      //获取每列的扩展字段名数组
      val newColNameArr = colValueCountMap.get(colName).get.toArray.sortBy(_._2).reverse.map(colName + "_if_" + _._1)
      newColNameArr.zipWithIndex.map { case (newColName, index) => {
        $"$arrColName".getItem(index).alias(newColName)
      }
      }
    })
    //扩展后的最终的dataframe
    val finalDF = transArrDF.select((orginalCols ++ extendExprs): _*)

    oneHotDF.unpersist()
    inputDF.unpersist()
    println(finalDF.count())
    println("test5 time : " + (System.currentTimeMillis() - startTime))
  }

  /**
    * @Author: TheBigBlue
    * @Description: 测试one-hot各种实现方式的时间
    * @Date: 2019/1/24
    * @param spark :
    * @Return:
    **/
  def test6(spark: SparkSession, inputDF: DataFrame, userSelectCols: Array[String], maxCategories: Int): Unit = {
    val start = System.currentTimeMillis()
    inputDF.cache()
    //校验空值
    val count = NullValueCheck.countNullValue(inputDF, userSelectCols)
    if (count > 0) {
      println("数据存在" + count + "条空值！")
      return
    }

    //多于用户设置的个数上限，则不编码
    val aggMap = userSelectCols.map((_ -> "approx_count_distinct")).toMap
    val aggRows = inputDF.agg(aggMap).take(1)

    //各列的计数
    val colsCountTuple: Array[(String, Long)] = userSelectCols.map(col => (col, aggRows(0).getAs[Long]("approx_count_distinct(" + col + ")")))
    //不参与编码的列
    val colsNotEncoded = colsCountTuple.filter(_._2 > maxCategories)
    println("不参与编码的列有：" + colsNotEncoded.mkString(","))
    //不参与编码的列的dataset
    val noEncodedDF = spark.createDataFrame(colsNotEncoded)
      .toDF("col_name", "distinct_count")

    //参与编码的列
    val transCols = colsCountTuple.filter(_._2 <= maxCategories).map(_._1)
    println("参与编码的列有：" + transCols.mkString(","))

    //离散列编码
    val (labelEncodeDF, inputCols) = encode(inputDF, transCols)

    //one-hot编码
    val estimator = new OneHotEncoderEstimator().setInputCols(inputCols).setOutputCols(inputCols.map(_ + "_encoded")).setDropLast(false)
    val oneHotDF = estimator.fit(labelEncodeDF).transform(labelEncodeDF).drop(inputCols: _*).cache()

    //获取各列不同值的统计数据
    val colValueCountMap = oneHotDF.rdd.treeAggregate(new LinkedHashMap[String, LinkedHashMap[String, Long]])(apply, merge)
    //    println(oneHotDF.count())
    val end = System.currentTimeMillis()
    println("test6 ready time: " + (end - start))


    val finalDF5 = test5_1(spark, inputDF, oneHotDF, transCols, colValueCountMap)
    println(finalDF5.count())
    val end5 = System.currentTimeMillis()
    println("test6 final5 time: " + (end5 - end))


    val finalDF4 = test4_1(spark, oneHotDF, transCols, colValueCountMap)
    println(finalDF4.count())
    val end4 = System.currentTimeMillis()
    println("test6 final4 time: " + (end4 - end5))


    val finalDF3 = test3_1(spark, oneHotDF, transCols, colValueCountMap)
    println(finalDF3.count())
    val end3 = System.currentTimeMillis()
    println("test6 final3 time: " + (end3 - end4))


    oneHotDF.unpersist()
    inputDF.unpersist()
    println("test6 time : " + (System.currentTimeMillis() - start))
  }

  def test3_1(spark: SparkSession, inputDF: DataFrame, transCols: Array[String], colValueCountMap: LinkedHashMap[String, LinkedHashMap[String, Long]]): DataFrame = {
    import spark.implicits._
    var oneHotDF = inputDF
    transCols.foreach(colName => {
      //vector列名
      val vectorColName = colName + "_indexed_encoded"
      //将vector转为arr的udf
      val vecToArray = udf((xs: SparseVector) => xs.toArray)
      //将该列vector转换为array
      oneHotDF = oneHotDF.withColumn(vectorColName, vecToArray($"$vectorColName"))

      //获取每列的扩展字段名数组
      val newColNameArr = colValueCountMap.get(colName).get.toArray.sortBy(_._2).reverse.map(colName + "_if_" + _._1)
      //扩展字段，删除向量列
      newColNameArr.zipWithIndex.foreach { case (newName, index) => {
        oneHotDF = oneHotDF.withColumn(newName, $"$vectorColName".getItem(index))
      }
      }
      oneHotDF = oneHotDF.drop(vectorColName)
    })
    oneHotDF
  }


  def test4_1(spark: SparkSession, inputDF: DataFrame, transCols: Array[String], colValueCountMap: LinkedHashMap[String, LinkedHashMap[String, Long]]): DataFrame = {
    import spark.implicits._
    var oneHotDF = inputDF
    transCols.foreach(colName => {
      val orginalCols = oneHotDF.schema.fieldNames.map(colName => $"$colName")
      //vector列名
      val vectorColName = colName + "_indexed_encoded"
      //将vector转为arr的udf
      val vecToArray = udf((xs: SparseVector) => xs.toArray)
      //将该列vector转换为array
      oneHotDF = oneHotDF.withColumn(vectorColName, vecToArray($"$vectorColName"))

      //获取每列的扩展字段名数组
      val newColNameArr = colValueCountMap.get(colName).get.toArray.sortBy(_._2).reverse.map(colName + "_if_" + _._1)
      val exprs: Array[Column] = newColNameArr.zipWithIndex.map { case (newColName, index) => {
        $"$vectorColName".getItem(index).alias(newColName)
      }
      }
      oneHotDF = oneHotDF.select((orginalCols ++ exprs): _*).drop(vectorColName)
    })
    oneHotDF
  }

  def test5_1(spark: SparkSession, inputDF: DataFrame, oneHotDF: DataFrame, transCols: Array[String], colValueCountMap: LinkedHashMap[String, LinkedHashMap[String, Long]]): DataFrame = {
    import spark.implicits._
    //将vector转为arr的udf
    val vecToArray = udf((xs: SparseVector) => xs.toArray)
    //一次性转换所有向量列
    val vecToArrExprs: Array[Column] = transCols.map(colName => {
      //vector列名
      val vectorColName = colName + "_indexed_encoded"
      vecToArray($"$vectorColName").alias(colName + "_arr_tmp")
    })
    val orginalCols = inputDF.schema.fieldNames.map(colName => $"$colName")
    //将vector转为arr后的dataframe
    val transArrDF = oneHotDF.select((orginalCols ++ vecToArrExprs): _*)

    //一次性扩展所有arr列
    val extendExprs: Array[Column] = transCols.flatMap(colName => {
      //arr列名
      val arrColName = colName + "_arr_tmp"
      //获取每列的扩展字段名数组
      val newColNameArr = colValueCountMap.get(colName).get.toArray.sortBy(_._2).reverse.map(colName + "_if_" + _._1)
      newColNameArr.zipWithIndex.map { case (newColName, index) => {
        $"$arrColName".getItem(index).alias(newColName)
      }
      }
    })
    //扩展后的最终的dataframe
    transArrDF.select((orginalCols ++ extendExprs): _*)
  }

  /**
    * @Author: TheBigBlue
    * @Description: 各分区计数
    * @Date: 2019/1/29
    * @param map :
    * @param row :
    * @Return:
    **/
  def apply(map: LinkedHashMap[String, LinkedHashMap[String, Long]], row: Row): LinkedHashMap[String, LinkedHashMap[String, Long]] = {
    //统计需要转换的列
    row.schema.fields.filter(_.dataType.typeName == "vector").foreach(field => {
      //原列名
      val originalColName = field.name.substring(0, field.name.indexOf("_indexed_encoded"))
      //该列的值
      val colValue = row.getAs(originalColName).toString
      //计数
      if (map.getOrElse(originalColName, null) == null) {
        val countMap = new LinkedHashMap[String, Long]()
        countMap.put(colValue, 1)
        map.put(originalColName, countMap)
      } else {
        val countMap = map.get(originalColName).get
        if (countMap.getOrElse(colValue, null) == null) {
          countMap.put(colValue, 1)
        } else {
          countMap.put(colValue, countMap.get(colValue).get + 1)
        }
      }
    })
    map
  }

  /**
    * @Author: TheBigBlue
    * @Description: 分区总计数
    * @Date: 2019/1/29
    * @param map1 :
    * @param map2 :
    * @Return:
    **/
  def merge(map1: LinkedHashMap[String, LinkedHashMap[String, Long]],
            map2: LinkedHashMap[String, LinkedHashMap[String, Long]]): LinkedHashMap[String, LinkedHashMap[String, Long]] = {
    map1 ++= map2
  }

  /**
    * @Author: TheBigBlue
    * @Description: 测试one-hot，使用rdd的reduceByKey计算，效率也很高
    * @Date: 2019/1/24
    * @param spark :
    * @Return:
    **/
  def test7(spark: SparkSession, inputDF: DataFrame, userSelectCols: Array[String], maxCategories: Int): Unit = {
    val startTime = System.currentTimeMillis()
    inputDF.cache()
    //校验空值
    val count = NullValueCheck.countNullValue(inputDF, userSelectCols)
    if (count > 0) {
      println("数据存在" + count + "条空值！")
      return
    }

    //多于用户设置的个数上限，则不编码
    val aggMap = userSelectCols.map((_ -> "approx_count_distinct")).toMap
    val aggRows = inputDF.agg(aggMap).take(1)

    //各列的计数
    val colsCountTuple: Array[(String, Long)] = userSelectCols.map(col => (col, aggRows(0).getAs[Long]("approx_count_distinct(" + col + ")")))
    //不参与编码的列
    val colsNotEncoded = colsCountTuple.filter(_._2 > maxCategories)
    println("不参与编码的列有：" + colsNotEncoded.mkString(","))
    import spark.implicits._
    //不参与编码的列的dataset
    val noEncodedDF = spark.createDataFrame(colsNotEncoded)
      .toDF("col_name", "distinct_count")

    //参与编码的列
    val transCols = colsCountTuple.filter(_._2 <= maxCategories).map(_._1)
    println("参与编码的列有：" + transCols.mkString(","))

    //离散列编码
    val (labelEncodeDF, inputCols) = encode(inputDF, transCols)

    //one-hot编码
    val estimator = new OneHotEncoderEstimator().setInputCols(inputCols).setOutputCols(inputCols.map(_ + "_encoded")).setDropLast(false)
    val oneHotDF = estimator.fit(labelEncodeDF).transform(labelEncodeDF).drop(inputCols: _*).cache()

    //将vector转为arr的udf
    val vecToArray = udf((xs: SparseVector) => xs.toArray)
    //一次性转换所有向量列
    val vecToArrExprs: Array[Column] = transCols.map(colName => {
      //vector列名
      val vectorColName = colName + "_indexed_encoded"
      vecToArray($"$vectorColName").alias(colName + "_arr_tmp")
    })
    val orginalCols = inputDF.schema.fieldNames.map(colName => $"$colName")
    //将vector转为arr后的dataframe
    val transArrDF = oneHotDF.select((orginalCols ++ vecToArrExprs): _*)

    val flatMapRDD: RDD[(String, Double)] = labelEncodeDF.rdd.flatMap(row => {
      transCols.map(colName => {
        val indexedColName = colName + "_indexed"
        (colName + "\001" + row.getAs(colName).toString, row.getAs[Double](indexedColName))
      })
    })
    val groupedRDD: RDD[(String, Iterable[(String, Double)])] = flatMapRDD.distinct().map(row => {
      val splitArr = row._1.split("\001")
      (splitArr(0), (splitArr(1), row._2))
    }).groupByKey()
    val sortedRDD: RDD[(String, Array[String])] = groupedRDD.map(row => (row._1, row._2.toArray.sortBy(_._2).map(_._1)))

    val colMap = sortedRDD.collectAsMap()
    val extendExprs = transCols.flatMap(colName => {
      val valueArr: Array[String] = colMap.get(colName).get
      valueArr.map(value => colName + "_if_" + value).zipWithIndex.map { case (newColName, index) => {
        val arrColName = colName + "_arr_tmp"
        $"$arrColName".getItem(index).alias(newColName)
      }
      }
    })
    val finalDF = transArrDF.select((orginalCols ++ extendExprs): _*)
    println(finalDF.count())

    oneHotDF.unpersist()
    inputDF.unpersist()
    println("test7 time : " + (System.currentTimeMillis() - startTime))
  }
}
