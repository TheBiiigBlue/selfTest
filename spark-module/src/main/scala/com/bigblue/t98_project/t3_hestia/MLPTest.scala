package com.bigblue.t98_project.t3_hestia

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.{Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.sql.SparkSession

/**
  * Created By TheBigBlue on 2019/3/29
  * Description : 
  */
object MLPTest {

  def testMLP(spark: SparkSession): Unit = {

    val inputDF = spark.table("sourcebase.classifier_train_local")
    inputDF.show
    val vecAss = new VectorAssembler().setInputCols(Array[String]("var_0", "var_1", "var_2", "var_3", "var_4")).setOutputCol("features")
    val classifier = new MultilayerPerceptronClassifier()
      //      .setBlockSize(0)
      //      .setInitialWeights(null)
      .setLayers(Array[Int](5, 2, 3)) //层规模(第一个元素是输入的特征维度，最后一个特征是输出的取值个数，对应阈值的Array.size)
      .setMaxIter(10) //最大迭代次数
      //      .setSeed(100) //随机种子
      //      .setSolver("")
      .setStepSize(100000) //步长
      .setTol(0.2) //算法收敛阈值
      .setThresholds(Array[Double](0.2, 0.3, 0.4)) //阈值
      .setLabelCol("target") //标签列
      .setFeaturesCol("features") //特征列
    //      .setPredictionCol("") //预测列
    //      .setProbabilityCol("")
    //      .setRawPredictionCol("")

    val pipelineModel = new Pipeline().setStages(Array[PipelineStage](vecAss, classifier)).fit(inputDF)
    pipelineModel.write.overwrite().save("/TheBigBlue/Hestia/model-save/mlpModel")

    val model = PipelineModel.read.load("/TheBigBlue/Hestia/model-save/mlpModel")
    val testDF = spark.table("sourcebase.classifier_test_local")
    val predictDF = model.transform(testDF)
    predictDF.show
  }

}
