package com.bigblue.t98_project.t1_artemis

import com.bigblue.t98_project.t1_artemis.cmpt.{DataSplitCmpt, GbdtImpCmpt, WoeCalcCmpt3, WoeTransCmpt}
import com.bigblue.t98_project.t1_artemis.constant.FeaturesConstant
import com.bigblue.t98_project.t1_artemis.model.GbdtClassifierCmpt
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ArrayBuffer

/**
  * Created By TheBigBlue on 2018/10/23 : 9:55
  * Description :
  */
object ArtemisTest {

  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis()
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    val originalData: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      //                .csv("file:///D:/Cache/ProgramCache/TestData/dataSource/arthemis/一手车201803.csv")
      .csv("file:///D:/Cache/ProgramCache/TestData/dataSource/woe-test/woe_little_test.csv")
      .cache()

    /**
      * 模拟特征工程之后的数据拆分
      */
    //val configMap: Map[String, Any] = Map("splitMode" -> "1", "trainingDataRatio" -> 0.8, "orderedCol" -> "申请书编号")
    val configMap: Map[String, Any] = Map("splitMode" -> "0", "trainingDataRatio" -> 0.8, "seed" -> 2)
    val time1 = System.currentTimeMillis()
    val (trainningDataDF, testDataDF) = DataSplitCmpt.dataSplit(spark, originalData, configMap)
    val time2 = System.currentTimeMillis()
    println("data split time :  " + (time2 - time1))
    trainningDataDF.show(false)
    val time3 = System.currentTimeMillis()
    println("data split show time :  " + (time3 - time2))

    /**
      * 模拟WOE计算
      */
    val (disCols, conCols) = getConAndDisCols(trainningDataDF)
    val woeCalcConfigMap: Map[String, Any] = Map(WoeCalcCmpt3.DIS_COLS -> disCols, WoeCalcCmpt3.CON_COLS -> conCols,
      WoeCalcCmpt3.LABEL_COL -> "逾期标志", WoeCalcCmpt3.SEG_MODE -> "0", WoeCalcCmpt3.SEGMENTS -> 10)
    val time4 = System.currentTimeMillis()
    val woeDictDF: DataFrame = WoeCalcCmpt3.woeCalc(spark, trainningDataDF, woeCalcConfigMap)
    val time5 = System.currentTimeMillis()
    println("woe calc time :  " + (time5 - time4))
    woeDictDF.show(false)
    val time6 = System.currentTimeMillis()
    println("woe show time :  " + (time6 - time5))

    /**
      * 模拟WOE转换
      */
    val transCols: Array[String] = disCols ++ conCols
    val woeTransConfigMap: Map[String, Any] = Map(WoeTransCmpt.TRANS_COLS -> transCols)
    val time7 = System.currentTimeMillis()
    val woeTransDF: DataFrame = WoeTransCmpt.woeTransform1(spark, trainningDataDF, woeDictDF, woeTransConfigMap)
    val time8 = System.currentTimeMillis()
    println("woe trans time :  " + (time8 - time7))
    woeTransDF.show(false)
    val time9 = System.currentTimeMillis()
    println("woe trans show time :  " + (time9 - time8))

    /**
      * 模拟GBDT特征重要性
      */
    val gbdtConfigMap: Map[String, Any] = Map(GbdtImpCmpt.LABEL_COL -> "逾期标志", GbdtImpCmpt.MAX_ITER -> 5, GbdtImpCmpt.MAX_DEPTH -> 5,
      GbdtImpCmpt.STEP_SIZE -> 0.05, GbdtImpCmpt.SEED -> 2, GbdtImpCmpt.FEATURE_SELECT_RATIO -> 1.0, GbdtImpCmpt.TRANS_COLS -> transCols)
    //allImportancesDF是显示使用的，filteredImportancesDF是输出使用的
    val time10 = System.currentTimeMillis()
    val (allImpDF, filteredDF) = GbdtImpCmpt.gbdtImpCalc(spark, woeTransDF, gbdtConfigMap)
    val time11 = System.currentTimeMillis()
    println("gbdt importance time :  " + (time11 - time10))
    allImpDF.show(50, false)
    filteredDF.show(false)
    val time12 = System.currentTimeMillis()
    println("gbdt importance show time :  " + (time12 - time11))

    /**
      * 模拟GBDT二分类模型
      */
    val gbdtModelConfigMap: Map[String, Any] = Map(GbdtImpCmpt.LABEL_COL -> "逾期标志")
    val time13 = System.currentTimeMillis()
    val model: PipelineModel = GbdtClassifierCmpt.trainGBDTModel(filteredDF, gbdtModelConfigMap)
    val time14 = System.currentTimeMillis()
    println("gbdt train model time :  " + (time14 - time13))

    /**
      * Created By TheBigBlue On 2018/11/8
      * Description : 模拟模型预测
      */
    val testTransDF: DataFrame = WoeTransCmpt.woeTransform1(spark, testDataDF, woeDictDF, woeTransConfigMap)
    //Make predictions
    val time15 = System.currentTimeMillis()
    val predictions: DataFrame = model.transform(testTransDF)
    val time16 = System.currentTimeMillis()
    println("gbdt model predict time :  " + (time16 - time15))
    predictions.show(500, false)
    println("总时长： " + (System.currentTimeMillis() - startTime))
    spark.close()
  }

  def getConAndDisCols(trainningDataDF: DataFrame): (Array[String], Array[String]) = {
    val dis_cols = new ArrayBuffer[String]()
    val con_cols = new ArrayBuffer[String]()
    trainningDataDF.columns.map(col => {
      //            if (FeaturesConstant.all_dis_name.contains(col)) {
      if (FeaturesConstant.test_dis_arr.contains(col)) {
        dis_cols += col
        //            } else if (FeaturesConstant.all_con_name.contains(col)) {
      } else if (FeaturesConstant.test_con_arr.contains(col)) {
        con_cols += col
      }
    })
    (dis_cols.toArray, con_cols.toArray)
  }
}
