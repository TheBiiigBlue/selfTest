package com.bigblue.t99_other_test

import com.bigblue.t98_project.t1_artemis.constant.FeaturesConstant
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * Created By TheBigBlue on 2018/8/28 : 9:09
  * Description : 
  */
object SimpleTest {

  def main(args: Array[String]): Unit = {
    //        testContinue()
    //        testArrayRemove()
    //        testArraySum()
    //        testScalaFor()
    //        testRowGet()
    //        testUDF()
    //        testTransformColumn()
    //        testStructType()
    //        testDoubleToInt()
    //        testRDDZip()
    testDataFrameMerge()
  }

  def testContinue(): Unit = {
    val list = List(11, 22, 33, 44, 55)
    //        for (l <- list) {
    //        for (l <- 10.to(30).reverse) {
    for (l <- 0.to(list.length - 1)) {
      breakable {
        if (l == 22) {
          break
        } else {
          println(l)
        }
      }
    }
  }

  def testArrayRemove(): Unit = {
    val binsam_count = new ArrayBuffer[Int]()
    //        binsam_count ++= Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
    binsam_count += 1
    println(binsam_count)
    val binsam_count_no_last = binsam_count - binsam_count(binsam_count.length - 1)
    println(binsam_count_no_last)
    println(binsam_count_no_last.sum)
    val s_mark = 10
    for (i <- binsam_count_no_last.sum.to(s_mark - 1).reverse) {
      println(i)
    }
  }

  def testArraySum(): Unit = {
    val arrBuff = new ArrayBuffer[Int]()
    arrBuff += (1, 2, 3, 4)
    println(arrBuff.sum)
  }

  def testScalaFor(): Unit = {
    val arrBuff = new ArrayBuffer[Int]()
    arrBuff += (1, 2, 3, 4)
    val arrBuff_no_last = arrBuff - arrBuff(arrBuff.size - 1)
    for (i <- arrBuff_no_last.sum.to(20 - 1).reverse) {
      println(i)
    }
    //        arrBuff
  }

  def testRowGet(): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    val originalData: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\一手车201803.csv")
      .cache()

    val col_data: Array[Row] = originalData.select("申请金额").sort().collect()
    breakable {
      for (i <- 0 to col_data.length) {
        val row1: Long = col_data(i).get(0).asInstanceOf[Int]
        println(s"row1: $row1")
        if (20 < i) {
          break
        }
      }
    }
  }

  def testUDF(): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    val originalData: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\一手车201803.csv")
      .cache()
    originalData.createOrReplaceTempView("originalData")
    originalData.select("申请金额").show()

    val bound_num = Array[Double](55300, 65000, 70000, 75000, 80000, 85000)
    spark.udf.register("trans", (x: Int) => {
      var bound_num_value: Long = 0
      breakable {
        for (i <- 0.to(bound_num.size - 1)) {
          if (i == 0 && x <= bound_num(i).longValue()) {
            bound_num_value = bound_num(i).longValue()
            break
          } else if (x <= bound_num(i).longValue() && x > bound_num(i - 1).longValue()) {
            bound_num_value = bound_num(i).longValue()
            break
          }
        }
      }
      bound_num_value
    })
    spark.sql("select trans(`申请金额`) from originalData limit 10").show
  }

  def testTransformColumn(): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    val originalData: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\一手车201803.csv")
      .cache()
    originalData.select("申请金额").show(10)
    val bound_num = Array[Double](55300, 65000, 70000, 75000, 80000, 85000)
    //        spark.sqlContext.udf.register("conToDis2", conToDis2 _)
    //        val temp = "conToDis2(`申请金额`, " + bound_num.mkString("|") + ")"

  }

  def conToDis1(x: Int, bound_num_arr: Array[Double]): Long = {
    var bound_num: Long = 0
    breakable {
      for (i <- 0 to bound_num_arr.size - 1) {
        if (i == 0 && x <= bound_num_arr(i).longValue()) {
          bound_num = bound_num_arr(i).longValue()
          break
        } else if (x <= bound_num_arr(i).longValue() && x > bound_num_arr(i - 1).longValue()) {
          bound_num = bound_num_arr(i).longValue()
          break
        }
      }
    }
    bound_num
  }

  def conToDis2(x: Int, bound_num_str: String): Int = {
    val bound_num_arr: Array[Int] = bound_num_str.split("|").map(_.toInt)
    var bound_num = 0
    breakable {
      for (i <- 0 to bound_num_arr.size - 1) {
        if (i == 0 && x <= bound_num_arr(i)) {
          bound_num = bound_num_arr(i)
          break
        } else if (x <= bound_num_arr(i) && x > bound_num_arr(i - 1)) {
          bound_num = bound_num_arr(i)
          break
        }
      }
    }
    bound_num
  }

  def testStructType(): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    var originalData: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\utrain_updated.csv")
      //                .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\一手车201803.csv")
      .cache()
    val disCols: Array[String] = originalData.columns.filter(col => FeaturesConstant.all_dis_name.toList.contains(col))
    disCols.filter(col => {
      originalData.schema.apply(col).dataType != StringType
    }).map(col => {
      originalData = originalData.withColumn(col, originalData(col).cast(StringType))
    })
    //        disCols.map(col => {
    //            println(col + "\t ==> \t" + originalData.schema.apply(col).dataType)
    //        })
    val posDF: Dataset[Row] = originalData.filter(originalData("逾期标志") === 1)
    val negDF: Dataset[Row] = originalData.filter(originalData("逾期标志") === 0)
    posDF.createOrReplaceTempView("posDF")
    spark.sql("select \"申请书编号\",`申请书编号`,`逾期标志` from posDF").show()
    //        negDF.select("申请书编号", "逾期标志").show(10)

    //        val colField: StructField = originalData.schema.apply("申请金额")
    //        println(colField.dataType == IntegerType)
    //        originalData.printSchema()
  }

  def testDoubleToInt(): Unit = {
    val doubleValue: Double = 123.00
    println(doubleValue.toInt)
    println(doubleValue.intValue())

    val doubleValue1 = 123.321
    println(doubleValue1.toInt)
    println(doubleValue1.intValue())
  }


  def testRDDZip(): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    val originalData: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\一手车201803.csv")
      .cache()

    val col = "申请金额"
    val df: DataFrame = originalData.select(col)

    val zipedRDD: RDD[Row] = df.rdd.zipWithIndex().map(x => {
      Row(x._1.get(0), x._2)
    })
    val structType = StructType(
      Array(
        StructField(col, df.schema.apply(col).dataType),
        StructField("index", LongType)
      )
    )
    val zipedDF: DataFrame = spark.createDataFrame(zipedRDD, structType)

    val rows: Array[Row] = zipedDF.filter(zipedDF("index") === 1).take(1)
    println(rows(0).get(0))
  }

  def testDataFrameMerge(): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    val originalData: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\一手车201803.csv")
      .cache()
    originalData.printSchema()
    originalData.drop("申请书编号", "逾期标志").printSchema()
    var df = originalData
    df.columns.filter(x => FeaturesConstant.all_dis_name.toList.contains(x))
      .filter(col => df.schema.apply(col).dataType != StringType).map(col => {
      df = df.withColumn(col, df(col).cast(StringType))
    })
    df.printSchema()

    originalData.createOrReplaceTempView("originalData")
    val columnArr = Array[String]("申请书编号", "申请金额").map(x => "`" + x + "`")
    spark.sql("select " + columnArr.mkString(",") + " from originalData limit 10").show()
  }
}
