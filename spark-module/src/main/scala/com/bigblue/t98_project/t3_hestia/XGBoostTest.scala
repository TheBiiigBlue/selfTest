package com.bigblue.t98_project.t3_hestia

import ml.dmlc.xgboost4j.scala.spark.{XGBoostClassificationModel, XGBoostClassifier}
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorAssembler}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.SparkSession

/**
  * Created By TheBigBlue on 2019/4/2
  * Description : 
  */
object XGBoostTest {

  def testXGBoost(spark: SparkSession, numClass: Int): Unit = {
    val inputDF = spark.table("sourcebase.classifier_train_local")
    inputDF.show

    val stringIndexer = new StringIndexer().
      setInputCol("target").
      setOutputCol("targetIndexed").
      fit(inputDF)
    val labelTransformed = stringIndexer.transform(inputDF).drop("target")

    val vectorAssembler = new VectorAssembler().
      setInputCols(Array("var_0", "var_1", "var_2", "var_3", "var_4")).
      setOutputCol("features")
    val xgbInput = vectorAssembler.transform(labelTransformed).select("features", "targetIndexed")

    val xgbParam = Map("eta" -> 0.1f,
      "max_depth" -> 2,
      "objective" -> "multi:softprob",
      "num_class" -> numClass,
      "num_round" -> 100,
      "num_workers" -> 2)

    val xgbClassifier = new XGBoostClassifier(xgbParam)
      .setFeaturesCol("features")
      .setLabelCol("targetIndexed")

    val xgbClassificationModel = xgbClassifier.fit(xgbInput)
    xgbClassificationModel.write.overwrite().save("/TheBigBlue/Hestia/model-save/xgbModel")

    val model = XGBoostClassificationModel.read.load("/TheBigBlue/Hestia/model-save/xgbModel")
    //    val testDF = spark.table("sourcebase.classifier_test_local")
    val predictDF = model.transform(xgbInput)
    predictDF.show

  }

  def testXGBoostPipeline(spark: SparkSession, numClass: Int): Unit = {
    val inputDF = spark.table("sourcebase.classifier_train_local")
    inputDF.show

    val Array(training, testDF) = inputDF.randomSplit(Array(0.8, 0.2), 123)

    val vectorAssembler = new VectorAssembler().
      setInputCols(Array("var_0", "var_1", "var_2", "var_3", "var_4")).
      setOutputCol("features")

    val stringIndexer = new StringIndexer()
      .setInputCol("target")
      .setOutputCol("targetIndexed")
      .fit(training)

    val xgbParam = Map("eta" -> 0.1f,
      "max_depth" -> 2,
      "objective" -> "multi:softprob",
      "num_class" -> numClass,
      "num_round" -> 100,
      "num_workers" -> 2)

    val xgbClassifier = new XGBoostClassifier(xgbParam)
      .setFeaturesCol("features")
      .setLabelCol("targetIndexed")

    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("realLabel")
      .setLabels(stringIndexer.labels)

    val pipelineModel = new Pipeline().setStages(Array(vectorAssembler, stringIndexer, xgbClassifier, labelConverter)).fit(training)
    pipelineModel.write.overwrite().save("/TheBigBlue/Hestia/model-save/xgbModel")

    val model = PipelineModel.read.load("/TheBigBlue/Hestia/model-save/xgbModel")
    //    val testDF = spark.table("sourcebase.classifier_test_local")
    val predictDF = model.transform(testDF)
    predictDF.show
  }

}
