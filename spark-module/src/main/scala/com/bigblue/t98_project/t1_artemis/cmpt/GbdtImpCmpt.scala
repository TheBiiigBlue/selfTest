package com.bigblue.t98_project.t1_artemis.cmpt

import org.apache.spark.ml.classification.{GBTClassificationModel, GBTClassifier}
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorAssembler, VectorIndexer}
import org.apache.spark.ml.{Pipeline, PipelineModel, linalg}
import org.apache.spark.sql._
import org.apache.spark.sql.types.{DoubleType, IntegerType}

/**
  * Created By TheBigBlue on 2018/10/26 : 17:14
  * Description : GBDT特征重要性组件
  */
object GbdtImpCmpt{

    /**
      * GBDT(特征重要性)组件--配置名称
      */
    //需要转换的列
    val TRANS_COLS: String = "transCols"
    //标签列
    val LABEL_COL: String = "labelCol"
    //最大迭代次数
    val MAX_ITER: String = "maxIter"
    //树的最大深度
    val MAX_DEPTH: String = "maxDepth"
    //学习率(步长)
    val STEP_SIZE: String = "stepSize"
    //随机种子
    val SEED: String = "seed"
    //训练集比例
    val TRAINING_DATA_RATIO: String = "trainingDataRatio"
    //特征选取比例
    val FEATURE_SELECT_RATIO: String = "featureSelectRatio"
    //子节点最少包含的实例数量
    val MIN_INSTANCES_PER_NODE: String = "minInstancesPerNode"
    //分裂节点时所需最小信息增益
    val MIN_INFO_GAIN: String = "minInfoGain"
    //最大二进制数
    val MAX_BINS: String = "maxBins"
    //树节点拆分的功能数
    val FEATURE_SUBSET_STRATEGY: String = "featureSubsetStrategy"

    /**
      * GBDT(特征重要性)组件--配置可选项
      */
    //信息增益计算标准
    val IMPURITY_ENTROPY: String = "entropy"
    val IMPURITY_GINI: String = "gini"
    //树节点拆分的功能数
    val FEATURE_SUBSET_STRATEGY_AUTO: String = "auto"
    val FEATURE_SUBSET_STRATEGY_ALL: String = "all"
    val FEATURE_SUBSET_STRATEGY_ONETHIRD: String = "onethird"
    val FEATURE_SUBSET_STRATEGY_SQRT: String = "sqrt"
    val FEATURE_SUBSET_STRATEGY_LOG2: String = "log2"

    /**
      * 模拟测试GBDT特征重要性
      * 显示所有特征，输出筛选后的特征
      * 开发时用这个
      */
    def gbdtImpCalc(spark: SparkSession, inputDF: DataFrame, gbdtConfigMap: Map[String, Any]): (DataFrame, DataFrame) = {
        //标签列
        val labelCol: String = gbdtConfigMap.getOrElse(GbdtImpCmpt.LABEL_COL, null).asInstanceOf[String]
        //特征选取比例
        val featureSelectRatio: Double = gbdtConfigMap.getOrElse(FEATURE_SELECT_RATIO, 1.0).asInstanceOf[Double]
        //训练模型
        val (pipelineModel, userTransDF) = trainPipelineModelByExample(inputDF, gbdtConfigMap)
        //获取特征重要性
        val gbdtModel: GBTClassificationModel = pipelineModel.stages(1).asInstanceOf[GBTClassificationModel]
        val importances: linalg.Vector = gbdtModel.featureImportances

        //将所有特征重要性字段下标转为字段名称
        val colsNameArr: Array[String] = importances.toSparse.indices.map(index => userTransDF.schema.fieldNames(index))
        //特征重要性值
        val impValues: Array[Double] = importances.toSparse.values
        //字段和对应的值拉链拼接
        val colAndValueArr: Array[(String, Double)] = colsNameArr.zip(impValues)
        //gbdt平均值
        val avgGBTValue: Double = average(impValues)
        //筛选大于平均值的重要特征的字段
        val filteredCols: Array[String] = colAndValueArr.filter(_._2 >= avgGBTValue * featureSelectRatio).map(_._1)
        //获取筛选重要特征后的数据
        val filteredDF: DataFrame = userTransDF.select(labelCol, filteredCols: _*)

        //对特征重要性排序后构建新的DataFrame
        var index = 0
        val importceShow: Array[GBTImportancesShow] = colAndValueArr.sortWith(_._2 > _._2).map(row => {
            index += 1
            GBTImportancesShow(index, row._1, row._2)
        })
        val allImpDF: DataFrame = spark.createDataFrame(importceShow)
        //将gbdt重要性DF和经重要性过滤后的数据返回
        (allImpDF, filteredDF)
    }

    /**
      * Created By TheBigBlue On 2018/11/8
      * Description : 使用pipeline训练gdbt模型
      * 返回：gbdt模型，用户选择的列和ID的DF，用户选择的列的集合
      */
    def trainPipelineModel(inputDF: DataFrame, gbdtConfigMap: Map[String, Any]): (PipelineModel, DataFrame) = {
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
                .setMaxIter(maxIter)    //迭代次数（>=0）
                .setMaxDepth(maxDepth)  //树的最大深度（>=0）
                .setStepSize(stepSize)  //每次迭代优化步长(学习率)
                .setSeed(seed)          //随机种子
                .setSubsamplingRate(trainingDataRatio) //训练数据比例，范围[0,1]
                .setMinInstancesPerNode(minInstancesPerNode)  //子节点最少包含的实例数量(叶节点最少样本数[1,1000])
                .setMinInfoGain(minInfoGain)    //分裂节点时所需最小信息增益
                .setMaxBins(maxBins)    //最大二进制数(一个特征分裂的最大数量[1,1000])
                .setFeatureSubsetStrategy(featureSubsetStrategy) //树节点拆分的功能数。 支持的选项：auto，all，onethird，sqrt，log2，（0.0-1.0），[1-n]。（默认值：all）
        val pipeline: Pipeline = new Pipeline().setStages(Array(featureAssembler, gbtClassifier))
        val model: PipelineModel = pipeline.fit(userTransDF)
        val filePath = "D:/Cache/ProgramCache/Artemis/gbdt-model/gbdt_model.xml"
        (model, userTransDF)
    }

    /**
      * Created By TheBigBlue On 2018/11/8
      * Description : 使用pipeline训练gdbt模型
      * 返回：gbdt模型，用户选择的列和ID的DF，用户选择的列的集合
      */
    def trainPipelineModelByExample(inputDF: DataFrame, gbdtConfigMap: Map[String, Any]): (PipelineModel, DataFrame) = {
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
        val data = new VectorAssembler().setInputCols(transCols).setOutputCol("features").transform(userTransDF)

        val labelIndexer = new StringIndexer()
          .setInputCol(labelCol)
          .setOutputCol("indexedLabel")
          .fit(data)

        val featureIndexer = new VectorIndexer()
          .setInputCol("features")
          .setOutputCol("indexedFeatures")
          .fit(data)

        val gbtClassifier: GBTClassifier = new GBTClassifier()
          .setLabelCol("indexedLabel")
          .setFeaturesCol("indexedFeatures")
          //          .setLabelCol(labelCol)
          //          .setFeaturesCol("features")
          .setMaxIter(maxIter)    //迭代次数（>=0）
          .setMaxDepth(maxDepth)  //树的最大深度（>=0）
          .setStepSize(stepSize)  //每次迭代优化步长(学习率)
          .setSeed(seed)          //随机种子
          .setSubsamplingRate(trainingDataRatio) //训练数据比例，范围[0,1]
          .setMinInstancesPerNode(minInstancesPerNode)  //子节点最少包含的实例数量(叶节点最少样本数[1,1000])
          .setMinInfoGain(minInfoGain)    //分裂节点时所需最小信息增益
          .setMaxBins(maxBins)    //最大二进制数(一个特征分裂的最大数量[1,1000])
          .setFeatureSubsetStrategy(featureSubsetStrategy) //树节点拆分的功能数。 支持的选项：auto，all，onethird，sqrt，log2，（0.0-1.0），[1-n]。（默认值：all）

        val labelConverter = new IndexToString()
          .setInputCol("prediction")
          .setOutputCol("predictedLabel")
          .setLabels(labelIndexer.labels)

        val pipeline1 = new Pipeline()
          .setStages(Array(labelIndexer, featureIndexer, gbtClassifier, labelConverter))
        val model = pipeline1.fit(data)
        (model, data)
    }

    /**
      * Created By TheBigBlue On 2018/11/8
      * Description : 训练gdbt模型
      * 返回：gbdt模型，用户选择的列和ID的DF，用户选择的列的集合
      */
    def trainGBTModel(inputDF: DataFrame, gbdtConfigMap: Map[String, Any]): (GBTClassificationModel, DataFrame, Array[String]) = {
        var transDF: DataFrame = inputDF
        //获取配置信息
        val transCols: Array[String] = gbdtConfigMap.getOrElse(TRANS_COLS, null).asInstanceOf[Array[String]]
        val labelCol: String = gbdtConfigMap.getOrElse(LABEL_COL, null).asInstanceOf[String]
        //最大迭代次数
        val maxIter: Int = gbdtConfigMap.getOrElse(MAX_ITER, 20).asInstanceOf[Int]
        //树的最大深度
        val maxDepth: Int = gbdtConfigMap.getOrElse(MAX_DEPTH, 5).asInstanceOf[Int]
        //学习率(步长)
        val stepSize: Double = gbdtConfigMap.getOrElse(STEP_SIZE, 0.1).asInstanceOf[Double]
        //随机种子
        val seed: Long = gbdtConfigMap.getOrElse(SEED, -1287390502).asInstanceOf[Int]
        //训练集比例
        val trainingDataRatio: Double = gbdtConfigMap.getOrElse(TRAINING_DATA_RATIO, 1.0).asInstanceOf[Double]
        //子节点最少包含的实例数量
        val minInstancesPerNode: Int = gbdtConfigMap.getOrElse(MIN_INSTANCES_PER_NODE, 1).asInstanceOf[Int]
        //分裂节点时所需最小信息增益
        val minInfoGain: Double = gbdtConfigMap.getOrElse(MIN_INFO_GAIN, 0.0).asInstanceOf[Double]
        //最大二进制数
        val maxBins: Int = gbdtConfigMap.getOrElse(MAX_BINS, 32).asInstanceOf[Int]
        //树节点拆分的功能数
        val featureSubsetStrategy: String = gbdtConfigMap.getOrElse(FEATURE_SUBSET_STRATEGY, FEATURE_SUBSET_STRATEGY_ALL).asInstanceOf[String]
        //字段数组中去除标签列，防止用户未选择转换列导致下面标签列即出现在label列，又出现在features列
        var featuresCols: Array[String] = transDF.drop(labelCol).schema.fieldNames
        if (transCols == null || transCols.length == 0) {
            //用户没选择转换列，除了标签列全部转换
            featuresCols.foreach(colName => {
                transDF = transDF.withColumn(colName, transDF(colName).cast(DoubleType))
            })
        } else {
            //只对选择的转换
            featuresCols = transCols.filter(col => featuresCols.contains(col))
            featuresCols.foreach(colName => {
                transDF = transDF.withColumn(colName, transDF(colName).cast(DoubleType))
            })
        }
        //只选取用户选择的列做模型
        val userTransDF: DataFrame = transDF.select(labelCol, featuresCols: _*)
        val featuresDF: DataFrame = new VectorAssembler().setInputCols(featuresCols).setOutputCol("features").transform(userTransDF)
        val gbtClassifier: GBTClassifier = new GBTClassifier()
                .setLabelCol(labelCol)
                .setFeaturesCol("features")
                .setMaxIter(maxIter)    //迭代次数（>=0）
                .setMaxDepth(maxDepth)  //树的最大深度（>=0）
                .setStepSize(stepSize)  //每次迭代优化步长(学习率)
                .setSeed(seed)          //随机种子
//                .setImpurity(impurity)  //信息增益计算标准
                .setSubsamplingRate(trainingDataRatio) //训练数据比例，范围[0,1]
                .setMinInstancesPerNode(minInstancesPerNode)  //子节点最少包含的实例数量(叶节点最少样本数[1,1000])
                .setMinInfoGain(minInfoGain)    //分裂节点时所需最小信息增益
                .setMaxBins(maxBins)    //最大二进制数(一个特征分裂的最大数量[1,1000])
                .setFeatureSubsetStrategy(featureSubsetStrategy) //树节点拆分的功能数。 支持的选项：auto，all，onethird，sqrt，log2，（0.0-1.0），[1-n]。（默认值：all）
        //训练模型
        val gbdtModel: GBTClassificationModel = gbtClassifier.fit(featuresDF)
        (gbdtModel, userTransDF, featuresCols)
    }

    //GBDT重要性展示的DataFrame的schema
    case class GBTImportancesShow(id: Long, featureName: String, gbtValue: Double)

    def average(a: Array[Double]) = {
        var t = 0.0
        for (i <- a) {
            t += i
        }
        t / a.length
    }
}
