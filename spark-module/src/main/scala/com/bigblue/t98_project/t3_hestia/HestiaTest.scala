package com.bigblue.t98_project.t3_hestia

import org.apache.spark.ml.feature._
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created By TheBigBlue on 2019/1/17
  * Description :
  */
object HestiaTest {

  def main(args: Array[String]): Unit = {
    val warehouseLocate = "hdfs://hcluster/user/hive/warehouse"
    //获取sparkSession
    val spark = SparkSession.builder()/*.master("local")*/.appName("HestiaTest")
            .config("spark.sql.warehouse.dir",warehouseLocate)
//            .config("spark.jars", "hdfs://hcluster/TheBigBlue/Hestia/lib/")
            .enableHiveSupport()
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

//    testOneHotEncoding(spark)
//    testOneHotEncoding1(spark)
//    SVCTest.testSVC(spark)
//    OneVsRestTest.testOneVsRest(spark)
//    MLPTest.testMLP(spark)
    if(args != null && args.length > 0){
      if("true".equals(args(0))){
        XGBoostTest.testXGBoostPipeline(spark, args(1).toInt)
      }else {
        XGBoostTest.testXGBoost(spark, args(1).toInt)
      }
    }

  }

  /**
    * @Author: TheBigBlue
    * @Description: 测试one-hot算法
    * @Date: 2019/1/17
    * @param spark :
    * @Return:
    **/
  def testOneHotEncoding(spark: SparkSession): Unit = {
    val df = spark.createDataFrame(Seq(
      (0, "log"),
      (1, "text"),
      (2, "text"),
      (3, "soyo"),
      (4, "text"),
      (5, "log"),
      (6, "log"),
      (7, "log"),
      (8, "hadoop")
    )).toDF("id", "label")

    val indexer: StringIndexerModel = new StringIndexer().setInputCol("label").setOutputCol("label_index").fit(df)
    val dfOut = indexer.transform(df)
    dfOut.show()
    val estimator = new OneHotEncoderEstimator().setInputCols(Array("label_index")).setOutputCols(Array("label_vector")).setDropLast(false)
    val encoderDF = estimator.fit(dfOut).transform(dfOut)

    encoderDF.show()
    encoderDF.select("label_vector").foreach(row => {
      row.getAs[SparseVector]("label_vector").toArray.foreach(x => print(x + " "))
      println()
    })

    import spark.implicits._
    var flatMapDS = encoderDF.select("label_vector").map(row => {
      row.getAs[SparseVector]("label_vector").toArray
    }).toDF("label_vector")
    flatMapDS.show()
    val schema = encoderDF.dropDuplicates("label").orderBy("label_index").select("label").collect().map(_.getAs[String](0))
    schema.zipWithIndex.foreach(x => {
      flatMapDS = flatMapDS.withColumn(x._1, $"label_vector".getItem(x._2))
    })
    flatMapDS.show()


    /**
      * TODO 看看这个算法怎么计算每列有多少不同的值
      * VectorIndexer
      * 主要作用：提高决策树或随机森林等ML方法的分类效果。
      * VectorIndexer是对数据集特征向量中的类别（离散值）特征（index categorical features categorical features ）进行编号。
      * 它能够自动判断那些特征是离散值型的特征，并对他们进行编号，具体做法是通过设置一个maxCategories，
      * 征向量中某一个特征不重复取值个数小于maxCategories，则被重新编号为0～K（K<=maxCategories-1）。
      * 某一个特征不重复取值个数大于maxCategories，则该特征视为连续值，不会重新编号（不会发生任何改变）
      */
  }

  /**
    * @Author: TheBigBlue
    * @Description: 测试one-hot
    * @Date: 2019/1/23
    * @param spark:
    * @Return:
    **/
  def testOneHotEncoding1(spark: SparkSession): Unit = {
    var df = spark.createDataFrame(Seq(
      (0, "log", "555"),
      (1, "text", "555"),
      (2, "text", "111"),
      (3, "foyo", "222"),
      (4, "text", "333"),
      (5, "log", "333"),
      (6, "log", "333"),
      (7, "log", "444"),
      (8, "hadoop", "444")
    )).toDF("id", "text", "num")

    Array("text", "num").foreach(colName =>
      df = new StringIndexer().setInputCol(colName).setOutputCol(colName + "_index").fit(df).transform(df)
    )
    df.show()
    val rowToLong: collection.Map[Row, Long] = df.rdd.countByValue()
    val rdd = df.rdd.cache()
    val reduceByKeyRDD = rdd.flatMap(row => {
      row.schema.fieldNames.map(colName => {
        (colName + "##" + row.getAs(colName).toString, 1L)
      })
    }).reduceByKey(_ + _)
    val groupedRDD = reduceByKeyRDD.map(tuple => {
      val arr = tuple._1.split("##")
      (arr(0), (arr(1), tuple._2))
    }).groupByKey()
    val finalRDD: RDD[(String, Array[(String, Long)])] = groupedRDD.map { case (colName, iter) => {
      (colName, iter.toArray.sortBy(_._2).reverse)
    }
    }
    finalRDD.foreach(row => println(row._1 + "\t" + row._2.mkString(",")))
  }

}
