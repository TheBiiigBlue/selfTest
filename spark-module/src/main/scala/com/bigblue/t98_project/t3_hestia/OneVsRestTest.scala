package com.bigblue.t98_project.t3_hestia

import org.apache.spark.ml.classification.{LogisticRegression, OneVsRest}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.{Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.sql.SparkSession

/**
  * Created By TheBigBlue on 2019/3/29
  * Description : 
  */
object OneVsRestTest {

  def testOneVsRest(spark: SparkSession): Unit = {

    val inputDF = spark.table("sourcebase.classifier_train_local")
    inputDF.show
    val vecAss = new VectorAssembler().setInputCols(Array[String]("var_0", "var_1", "var_2", "var_3", "var_4")).setOutputCol("features")
    val oneVsRest = new OneVsRest()
      .setClassifier(new LogisticRegression())
      .setFeaturesCol("features")
      .setLabelCol("target")
    //      .setParallelism(2)
    //      .setPredictionCol("")
    //      .setRawPredictionCol("")
    //      .setWeightCol("")

    val pipelineModel = new Pipeline().setStages(Array[PipelineStage](vecAss, oneVsRest)).fit(inputDF)
    pipelineModel.write.overwrite().save("/TheBigBlue/Hestia/model-save/oneVsRestModel")


    val model = PipelineModel.read.load("/TheBigBlue/Hestia/model-save/oneVsRestModel")
    val testDF = spark.table("sourcebase.classifier_test_local")
    val predictDF = model.transform(testDF)
    predictDF.show

  }

}
