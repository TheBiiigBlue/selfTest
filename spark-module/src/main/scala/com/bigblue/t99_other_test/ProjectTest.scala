package com.bigblue.t99_other_test

import com.bigblue.t98_project.t1_artemis.cmpt.GbdtImpCmpt
import com.bigblue.t98_project.t1_artemis.cmpt.GbdtImpCmpt.GBTImportancesShow
import org.apache.spark.ml.classification.{GBTClassificationModel, GBTClassifier}
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, MulticlassClassificationEvaluator}
import org.apache.spark.ml.feature._
import org.apache.spark.ml.{Pipeline, PipelineModel, linalg}
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.collection.mutable.ArrayBuffer

/**
  * Created By TheBigBlue on 2018/7/30 : 10:41
  * Description : 
  */
object ProjectTest {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    //        testSQL(spark)
    //        testGBTClassifier(spark)
    //        testGBDTImportances(spark)
    //        testGBDTImportances1(spark)
    testGBTRoc(spark)
    //        testSVM(spark)
    //        testWoe(spark)

    spark.close()
  }

  def testSQL(spark: SparkSession) = {
    val df1: DataFrame = spark.read.format("com.databricks.spark.csv")
      .option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .load("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\dataframe1\\test2.csv")

    //null值填充
    df1.na.fill(Map[String, String]("age" -> "-99999")).show()

    //随机比例切分数据
    val array: Array[Dataset[Row]] = df1.randomSplit(Array(20, 80), 2)
    array(0).show()
    array(1).show()

    //顺序按比例拆分
    //        val rows: Array[Row] = df1.take(Math.round(df1.count() * 0.8).intValue())
    //        println(rows.toBuffer)

    df1.createOrReplaceTempView("df1")
    val trainningDataDF: DataFrame = spark.sql("select * from df1 limit " + (Math.round(df1.count() * 0.8).intValue()))
    val testDataDF: DataFrame = spark.sql("select * from df1 limit " + (Math.round(df1.count() * 0.8).intValue()) + "," + df1.count())

    //取差集
    //        val testDataDF: DataFrame = df1.except(trainningDataDF).toDF()
    trainningDataDF.show()
    testDataDF.show()

  }

  def testGBTClassifier(spark: SparkSession) = {
    val filePath = "file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\logisticRegression"
    val data: DataFrame = spark.read.format("libsvm").load(filePath)
    data.printSchema()
    data.show(10)

    // Index labels, adding metadata to the label column.
    // Fit on whole dataset to include all labels in index.
    val labelIndexer: StringIndexerModel = new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(data)
    val columns: Array[String] = data.columns

    // Automatically identify categorical features, and index them.
    // Set maxCategories so features with > 4 distinct values are treated as continuous.
    val featureIndexer: VectorIndexerModel = new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(4).fit(data)
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))

    // Train a GBT model.
    val gbt: GBTClassifier = new GBTClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)

    // Convert indexed labels back to original labels.
    val labelConverter: IndexToString = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)
    val pipeline: Pipeline = new Pipeline().setStages(Array(labelIndexer, featureIndexer, gbt, labelConverter))
    val model: PipelineModel = pipeline.fit(trainingData)

    // Make predictions
    val predictions: DataFrame = model.transform(testData)
    predictions.show()

    // Select example rows to display
    predictions.select("predictedLabel", "label", "features").show(5)

    // Select (prediction, true label) and compute test error
    val evaluator: MulticlassClassificationEvaluator = new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("accuracy")
    val accurary: Double = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accurary))

    val gbtModel: GBTClassificationModel = model.stages(2).asInstanceOf[GBTClassificationModel]
    println(gbtModel.treeWeights.map(print))
    println(gbtModel.trees(0).getClass)
    //        gbtModel.su
    println("Learned classification GBT model: \n" + gbtModel.toDebugString)
    val importances: linalg.Vector = gbtModel.featureImportances
    println(importances)
  }

  def testGBDTImportances(spark: SparkSession): Unit = {
    val filePath = "file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\gbdt\\一手车predicting_woe.csv"
    val data: DataFrame = spark.read.option("header", "true") //第一行作为Schema
      .option("inferSchema", "true") //推测schema类型
      .csv(filePath)
    //        val featureLabel: DataFrame = data.drop("逾期标志")
    //        featureLabel.printSchema()
    val allCols: Array[String] = data.columns
    val featuresCols: Array[String] = allCols.filter(_ != "逾期标志")
    val vecDF: DataFrame = new VectorAssembler().setInputCols(featuresCols).setOutputCol("features").transform(data)
    //        vecDF.show()
    // Train a GBT model.
    val gbt: GBTClassifier = new GBTClassifier()
      .setLabelCol("逾期标志")
      .setFeaturesCol("features")
      .setMaxIter(10)
      //迭代次数（>=0）
      //                .setImpurity("") //计算信息增益的准则
      .setLossType("log loss")
    //                .setThresholds(Array(0.1, 0.2))
    //损失函数类型
    //                .setMaxBins(0)//连续特征离散化的最大数量，以及选择每个节点分裂特征的方式。(一个特征分裂的最大数量[1,1000])
    //                .setMaxDepth(0)//树的最大深度（>=0）
    //                .setMinInfoGain(0.0)//分裂节点时所需最小信息增益
    //                .setMinInstancesPerNode(0)//分裂后自节点最少包含的实例数量(叶节点最少样本数[1,1000])
    //                .setSeed(0)//随机种子
    //                .setStepSize(0.0)//每次迭代优化步长(学习率)
    //                .setSubsamplingRate(0.2) //学习一棵决策树使用的训练数据比例，范围[0,1](训练采集样本比例)
    //                .setFeatureSubsetStrategy("0.2")//每个树节点处的拆分要考虑的功能数。 支持的选项：auto，all，onethird，sqrt，log2，（0.0-1.0），[1-n]。（默认值：all）
    //没有最大叶子数、树的数目、学习速率(0-1)，训练采集样本比例(0-1)、训练采集特征比例(0-1)、测试数据比例(0-1)、metric变量(NDCG、DCG)
    val gbdtModel: GBTClassificationModel = gbt.fit(vecDF)
    //        println(gbdtModel.explainParams())
    val importances: linalg.Vector = gbdtModel.featureImportances

    val colsArr = new ArrayBuffer[String]()
    val values: Array[Double] = importances.toSparse.values
    for (index <- importances.toSparse.indices) {
      colsArr += allCols(index)
    }
    var i = 0
    /**
      * 显示所有的字段，包含重要程度为0的
      */
    val allColsDF: Array[GBDTImportances] = featuresCols.zip(importances.toArray).map(x => {
      i = i + 1
      GBDTImportances(i, x._1, x._2)
    })
    val allImportancesDF: DataFrame = spark.createDataFrame(allColsDF)
    allImportancesDF.show()

    import spark.implicits._
    /**
      * 获取所有重要程度的平均值
      */
    val rows: Array[Row] = allImportancesDF.agg("gbdtValue" -> "avg").take(1)
    val avgValue: Double = rows(0).getAs[Double]("avg(gbdtValue)")
    println(avgValue)

    /**
      * 只显示重要程度大于等于指定比例的特征
      */
    i = -1
    val importancesDF: DataFrame = colsArr.zip(values).map(x => {
      i = i + 1
      GBDTImportances(i, x._1, x._2)
    }).toDF()
    //显示重要程度不为0的特征
    importancesDF.orderBy(importancesDF("gbdtValue").desc).show()
    importancesDF.orderBy(importancesDF("gbdtValue").desc).filter(importancesDF("gbdtValue") >= avgValue * 0.2).show()
  }

  /**
    * 模拟测试GBDT特征重要性
    * 显示筛选后的特征，输出筛选后的特征
    */
  def testGBDTImportances1(spark: SparkSession, allDataDF: DataFrame, gbdtConfigMap: Map[String, Any]): (DataFrame, DataFrame) = {
    //训练gdbt模型
    val (gbtModel, userTransDF, featureCols) = GbdtImpCmpt.trainGBTModel(allDataDF, gbdtConfigMap)

    //获取特征重要性相关数据
    val featureImportances: linalg.Vector = gbtModel.featureImportances
    //将返回的下标转化为字段名
    val colsArr = new ArrayBuffer[String]()
    for (index <- featureImportances.toSparse.indices) {
      colsArr += allDataDF.columns(index)
    }

    //将字段名和gbt重要性值拉链
    val importanceses: Array[GBTImportances] = featureCols.zip(featureImportances.toArray).map(x => {
      GBTImportances(x._1, x._2)
    })
    val allImportancesDF: DataFrame = spark.createDataFrame(importanceses)

    //获取所有重要程度的平均值
    val rows: Array[Row] = allImportancesDF.agg("gbtValue" -> "avg").take(1)
    val avgGBTValue: Double = rows(0).getAs[Double]("avg(gbtValue)")
    println("gbt平均值：" + avgGBTValue)

    /**
      * 获取大于指定比例的特征
      */
    val filteredImportancesDF: Dataset[Row] = allImportancesDF.filter(allImportancesDF("gbtValue") >= avgGBTValue * 0.6)
    val gbtShowRDD: RDD[GBTImportancesShow] = filteredImportancesDF.rdd.zipWithIndex().map(x => {
      GBTImportancesShow(x._2 + 1, x._1.getAs[String]("featureName"), x._1.getAs[Double]("gbtValue"))
    })
    val gbtShowDF: DataFrame = spark.createDataFrame(gbtShowRDD)

    allDataDF.createOrReplaceTempView("allDataDF")
    val sb = new StringBuilder()
    filteredImportancesDF.select("featureName").take(100).map(line => {
      sb.append("`" + line(0).asInstanceOf[String] + "`,")
    })
    val filteredDF: DataFrame = spark.sql("select " + sb.toString().substring(0, sb.toString().length - 1) + " from allDataDF")
    (gbtShowDF, filteredDF)
  }

  def testGBTRoc(spark: SparkSession) = {
    val filePath = "file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\logisticRegression"
    val data: DataFrame = spark.read.format("libsvm").load(filePath)

    val labelIndexer: StringIndexerModel = new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(data)

    val featureIndexer: VectorIndexerModel = new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(4).fit(data)
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))

    val gbt: GBTClassifier = new GBTClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)

    val labelConverter: IndexToString = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)
    val pipeline: Pipeline = new Pipeline().setStages(Array(labelIndexer, featureIndexer, gbt, labelConverter))
    val model: PipelineModel = pipeline.fit(trainingData)

    val predictions: DataFrame = model.transform(testData)
    predictions.show()
    predictions.select("predictedLabel", "label", "features").show(5)

    val evaluator: MulticlassClassificationEvaluator = new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("accuracy")
    val accurary: Double = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accurary))

    val gbtModel: GBTClassificationModel = model.stages(2).asInstanceOf[GBTClassificationModel]

    //        val binaryMetrics = new BinaryClassificationMetrics(
    //            predictions.select(predictions("probability"), predictions("label").cast(DoubleType)).rdd.map {
    //                case Row(score: Vector, label: Double) => (score(1), label)
    //            }
    //        )
    //        spark.createDataFrame(binaryMetrics.roc()).show()

    val binaryClassificationEvaluator = new BinaryClassificationEvaluator()

    def printlnMetric(metricName: String): Unit = {
      println(metricName + " = " + binaryClassificationEvaluator.setMetricName(metricName).evaluate(predictions))
    }

    printlnMetric("areaUnderROC")
    printlnMetric("areaUnderPR")

  }

  def testSVM(spark: SparkSession) = {
    new SVMWithSGD().optimizer
      .setConvergenceTol(0.001) // 设置收敛系数
      //.setGradient()      //设置梯度下降
      .setMiniBatchFraction(0.00) //设置每次迭代参与计算的样本比例，默认为1.0
      .setNumIterations(100) //设置迭代次数，默认为100
      .setRegParam(0.1) //设置正则化参数，默认为0.0，使用正则参数为0.1的L1正则来训练，如果设置1.0则是使用L2的正则化
      .setStepSize(0.00) //设置迭代步长，默认为1.0
    //.setUpdater()     //设置范数，SquaredL2Updater，设置正则化，L2范数
  }

  def testWoe(spark: SparkSession) = {
    import spark.implicits._
    /**
      * 模拟pandas.DataFrame
      */
    val data = Array(93660.0, 382760.0, 100000.0, 170000.0, 175000.0, 990000.0)
    val index: Range.Inclusive = 0 to data.size - 1
    val tuples: Seq[(Int, Double)] = index.zip(data)
    val ds: Dataset[(Int, Double)] = spark.createDataset(tuples)
    ds.describe().show()

    val rdd = spark.sparkContext.makeRDD(data)
    val sorted: RDD[(Long, Double)] = rdd.sortBy(identity).zipWithIndex().map {
      case (v, idx) => (idx, v)
    }
    val count = sorted.count()
    /**
      * 模拟pandas.dataframe.describe
      * 计算公式：b = (count - 1) * 0.x 整数部分记为c,小数部分记为d
      * result = sorted(c) + (sorted(c + 1) - sorted(c)) * d
      */
    //计算公式：
    val b = (BigDecimal(count - 1) * BigDecimal(0.5)).doubleValue()
    val bStr = b.toString
    val c = b.toInt
    val d = BigDecimal(0 + bStr.substring(bStr.indexOf("."))).doubleValue()
    val r = sorted.lookup(c).head + (sorted.lookup(c + 1).head - sorted.lookup(c).head) * d
    println(r)
  }

}

case class GBTImportances(featureName: String, gbtValue: Double)

case class GBDTImportances(id: Int, name: String, gbdtValue: Double)
