package com.bigblue.t98_project.t3_hestia

import org.apache.spark.ml.{Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.ml.classification.{LinearSVC, LinearSVCModel}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession

/**
  * Created By TheBigBlue on 2019/3/29
  * Description : 
  */
object SVCTest {

  def testSVC(spark: SparkSession): Unit = {

    val inputDF = spark.table("sourcebase.classifier_train_local")
    inputDF.show
    val vecAss = new VectorAssembler().setInputCols(Array[String]("var_0","var_1","var_2","var_3","var_4")).setOutputCol("features")
    val svcModel = new LinearSVC()
      .setLabelCol("target")
      .setFeaturesCol("features")
      .setFitIntercept(true) //是否拟合截距
      .setMaxIter(10) //最大迭代次数
      .setRegParam(1000000) //正则化参数
      .setStandardization(true) //标准化
      .setThreshold(1000) //阈值
      .setTol(1000) //结束标准的精度

    val pipelineModel = new Pipeline().setStages(Array[PipelineStage](vecAss, svcModel)).fit(inputDF)
    pipelineModel.write.overwrite().save("/TheBigBlue/Hestia/model-save/svcModel")


    val model = PipelineModel.read.load("/TheBigBlue/Hestia/model-save/svcModel")
    val testDF = spark.table("sourcebase.classifier_test_local")
    val predictDF = model.transform(testDF)
    predictDF.show
  }

}
