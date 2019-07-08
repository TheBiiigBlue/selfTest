package com.bigblue.t98_project.t3_hestia

import com.bigblue.t97_utils.NullValueCheck
import org.apache.spark.SparkException
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Created By TheBigBlue on 2019/1/23
  * Description : 
  */
object LabelEncoderTest {

  def main(args: Array[String]): Unit = {

    val warehouseLocate = "hdfs://hcluster/user/hive/warehouse"
    //获取sparkSession
    val spark = SparkSession.builder().master("local").appName("HestiaTest")
      .config("spark.sql.warehouse.dir", warehouseLocate)
      .enableHiveSupport()
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    //    test1(spark)
    test2(spark)
  }

  /**
    * @Author: TheBigBlue
    * @Description: 使用pipeline
    * @Date: 2019/1/23
    * @param spark :
    * @Return:
    **/
  def test1(spark: SparkSession): Unit = {
    //输入数据
    val inputDF = spark.createDataFrame(Seq(
      (0, "log", "555", 111),
      (1, "text", "555", 111),
      (2, "text", "111", 222),
      (3, "soyo", "222", 333),
      (4, "text", "333", 333),
      (5, "log", "333", 333),
      (6, "log", "333", 444),
      (7, "log", "444", 444),
      (8, "hadoop", "444", 444),
      (9, null, "444", 444)
    )).toDF("id", "text", "num_txt", "num")

    //TODO 校验空值

    //用户选择的需要转换的离散列
    val userSelectCols = Array("text", "num_txt", "num")
    val start = System.currentTimeMillis()
    //使用pipeline一次转换
    val indexers = userSelectCols.map(col => {
      new StringIndexer().setInputCol(col).setOutputCol(col + "_indexed")
    })
    val newDF = new Pipeline().setStages(indexers).fit(inputDF).transform(inputDF)
    val end = System.currentTimeMillis()
    println("trans time : " + (end - start))

    try {
      newDF.show()
    } catch {
      case e: SparkException => println("数据存在空值！")
      case ex: Exception => ex.printStackTrace()
    }

    //生成字典DF
    val colNames = newDF.schema.fieldNames
    val dictRDD: RDD[(String, String, Double)] = newDF.rdd.flatMap(row => {
      colNames.filter(_.indexOf("_indexed") > 0).map(colName => {
        val originalCol = colName.substring(0, colName.lastIndexOf("_indexed"))
        (originalCol, row.getAs(originalCol).toString, row.getAs[Double](colName))
      })
    })
    val dictDF = spark.createDataFrame(dictRDD)
      .toDF("columns", "properties", "labels")
      .dropDuplicates("columns", "properties", "labels")
      .orderBy("columns", "labels")
    dictDF.show()
    val end1 = System.currentTimeMillis()
    println("dict time : " + (end1 - end))
  }

  /**
    * @Author: TheBigBlue
    * @Description: 读取hive中的表测试
    * @Date: 2019/1/28
    * @param spark:
    * @Return:
    **/
  def test2(spark: SparkSession): Unit = {
    val tableDF = spark.table("viewbase.train_3")
    tableDF.show()
    //用户选择的需要转换的离散列
    val userSelectCols = Array("name", "sex", "ticket")
    val count = NullValueCheck.countNullValue(tableDF, userSelectCols)
    if (count > 0) {
      println("数据存在" + count + "条空值！")
      return
    }
    val indexers = userSelectCols.map(col => {
      new StringIndexer().setInputCol(col).setOutputCol(col + "_indexed")
    })
    val indexedDF = new Pipeline().setStages(indexers).fit(tableDF).transform(tableDF)
    indexedDF.show()

    //生成字典DF
    val dictRDD: RDD[(String, String, Double)] = indexedDF.rdd.flatMap(row => {
      userSelectCols.map(colName => {
        (colName, row.getAs(colName).toString, row.getAs[Double](colName + "_indexed"))
      })
    })
    val dictDF = spark.createDataFrame(dictRDD)
      .toDF("columns", "properties", "labels")
      .dropDuplicates("columns", "properties", "labels")
      .orderBy("columns", "labels")
    dictDF.show(500, false)
  }
}
