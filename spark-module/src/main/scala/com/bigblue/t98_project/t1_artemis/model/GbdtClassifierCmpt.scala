package com.bigblue.t98_project.t1_artemis.model

import com.bigblue.t98_project.t1_artemis.cmpt.GbdtImpCmpt
import org.apache.spark.ml.classification.GBTClassifier
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.sql.{Column, DataFrame}

/**
  * Created By TheBigBlue on 2018/10/31 : 9:38
  * Description : gbdt二分类模型
  */
object GbdtClassifierCmpt {

  /**
    * Created By TheBigBlue On 2018/10/31
    * Description : 训练gbdt模型
    */
  def trainGBDTModel(inputDF: DataFrame, gbdtConfigMap: Map[String, Any]): PipelineModel = {
    //获取配置信息 -- 要转换的列
    var transCols: Array[String] = gbdtConfigMap.getOrElse(GbdtImpCmpt.TRANS_COLS, null).asInstanceOf[Array[String]]
    //标签列
    val labelCol: String = gbdtConfigMap.getOrElse(GbdtImpCmpt.LABEL_COL, null).asInstanceOf[String]
    //最大迭代次数
    val maxIter: Int = gbdtConfigMap.getOrElse(GbdtImpCmpt.MAX_ITER, 20).asInstanceOf[Int]
    //树的最大深度
    val maxDepth: Int = gbdtConfigMap.getOrElse(GbdtImpCmpt.MAX_DEPTH, 5).asInstanceOf[Int]
    //学习率(步长)
    val stepSize: Double = gbdtConfigMap.getOrElse(GbdtImpCmpt.STEP_SIZE, 0.1).asInstanceOf[Double]
    //随机种子
    val seed: Long = gbdtConfigMap.getOrElse(GbdtImpCmpt.SEED, -1287390502).asInstanceOf[Int]
    //训练集比例
    val trainingDataRatio: Double = gbdtConfigMap.getOrElse(GbdtImpCmpt.TRAINING_DATA_RATIO, 1.0).asInstanceOf[Double]
    //子节点最少包含的实例数量
    val minInstancesPerNode: Int = gbdtConfigMap.getOrElse(GbdtImpCmpt.MIN_INSTANCES_PER_NODE, 1).asInstanceOf[Int]
    //分裂节点时所需最小信息增益
    val minInfoGain: Double = gbdtConfigMap.getOrElse(GbdtImpCmpt.MIN_INFO_GAIN, 0.0).asInstanceOf[Double]
    //最大二进制数
    val maxBins: Int = gbdtConfigMap.getOrElse(GbdtImpCmpt.MAX_BINS, 32).asInstanceOf[Int]
    //树节点拆分的功能数
    val featureSubsetStrategy: String = gbdtConfigMap.getOrElse(GbdtImpCmpt.FEATURE_SUBSET_STRATEGY, GbdtImpCmpt.FEATURE_SUBSET_STRATEGY_ALL).asInstanceOf[String]

    //拼接要转换的列，修改为double类型
    var transColArr: Array[Column] = null
    if (transCols == null || transCols.length == 0) {
      //用户没选择转换列，除了标签列全部转换
      transCols = inputDF.drop(labelCol).schema.fieldNames
      transColArr = transCols.map(col => inputDF(col).cast(DoubleType))
    } else {
      //只对选择的转换
      transColArr = transCols.map(col => inputDF(col).cast(DoubleType))
    }
    val transAndLabelCols: Array[Column] = transColArr :+ inputDF(labelCol).cast(IntegerType)
    //只选取用户选择的列做模型
    val userTransDF: DataFrame = inputDF.select(transAndLabelCols: _*).cache()
    val featureAssembler: VectorAssembler = new VectorAssembler().setInputCols(transCols).setOutputCol("features")
    val gbtClassifier: GBTClassifier = new GBTClassifier()
      .setLabelCol(labelCol)
      .setFeaturesCol("features")
      .setMaxIter(maxIter) //迭代次数（>=0）
      .setMaxDepth(maxDepth) //树的最大深度（>=0）
      .setStepSize(stepSize) //每次迭代优化步长(学习率)
      .setSeed(seed) //随机种子
      .setSubsamplingRate(trainingDataRatio) //训练数据比例，范围[0,1]
      .setMinInstancesPerNode(minInstancesPerNode) //子节点最少包含的实例数量(叶节点最少样本数[1,1000])
      .setMinInfoGain(minInfoGain) //分裂节点时所需最小信息增益
      .setMaxBins(maxBins) //最大二进制数(一个特征分裂的最大数量[1,1000])
      .setFeatureSubsetStrategy(featureSubsetStrategy) //树节点拆分的功能数。 支持的选项：auto，all，onethird，sqrt，log2，（0.0-1.0），[1-n]。（默认值：all）
    val pipeline: Pipeline = new Pipeline().setStages(Array(featureAssembler, gbtClassifier))
    val model: PipelineModel = pipeline.fit(userTransDF)
    val filePath = "D:/Cache/ProgramCache/Artemis/gbdt-model/gbdt_model.xml"
    model
  }
}
