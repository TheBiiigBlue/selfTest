package com.bigblue.t98_project.t1_artemis.cmpt

import java.util.Random
import org.apache.spark.sql._

/**
  * Created By TheBigBlue on 2018/10/23 : 9:13
  * Description : 数据拆分组件
  */
object DataSplitCmpt{

    /**
      * 数据拆分组件--配置名称
      */
    //拆分方式
    val SPLIT_MODE: String = "splitMode"
    //训练集比例
    val TRAINING_DATA_RATIO: String = "trainingDataRatio"
    //随机种子
    val SEED: String = "seed"
    //顺序按比例拆分-排序字段
    val ORDERED_COL: String = "orderedCol"

    /**
      * 数据拆分组件--配置可选项
      */
    //拆分方式-随机按比例拆分
    val SPLIT_MODE_RANDOM: String = "0"
    //拆分方式-顺序拆分
    val SPLIT_MODE_ORDERED: String = "1"

    /**
      * Created By TheBigBlue On 2018/10/23
      * Description : 输入DF，输出训练集DF和测试集DF
      */
    def dataSplit(spark: SparkSession, inputDF: DataFrame, configMap: Map[String, Any]): (DataFrame, DataFrame) = {
        //拆分方式
        val splitMode: String = configMap.get(SPLIT_MODE).get.asInstanceOf[String]
        //训练集比例
        var trainingDataRatio: Double = configMap.get(TRAINING_DATA_RATIO).get.asInstanceOf[Number].doubleValue().formatted("%.2f").toDouble
        if (trainingDataRatio < 0 || trainingDataRatio > 1) {
            trainingDataRatio = new Random().nextDouble().formatted("%.2f").toDouble
        }
        //随机种子
        val seed: Long = configMap.getOrElse(SEED, -999999).asInstanceOf[Number].longValue()
        //顺序拆分的排序字段
        var orderedCol: String = configMap.getOrElse(ORDERED_COL, null).asInstanceOf[String]

        var trainningDataDF: DataFrame = null
        var testDataDF: DataFrame = null

        splitMode match {
            case SPLIT_MODE_RANDOM => {
                var splitData: Array[Dataset[Row]] = null
                //随机按比例拆分
                if (seed == -999999) {
                    splitData = inputDF.randomSplit(Array(trainingDataRatio, 1 - trainingDataRatio))
                } else {
                    splitData = inputDF.randomSplit(Array(trainingDataRatio, 1 - trainingDataRatio), seed)
                }
                //训练数据DataSet
                trainningDataDF = splitData(0)
                //测试数据DataSet
                testDataDF = splitData(1)
            }
            case SPLIT_MODE_ORDERED => {
                //重用缓存
                val inputCacheDF = inputDF.cache()
                //顺序按比例拆分
                if (orderedCol == null) {
                    val fieldNameArr: Array[String] = inputCacheDF.schema.fieldNames
                    //没有指定排序字段，按第一列排序
                    orderedCol = fieldNameArr(0)
                }
                //输入数据总条数
                val inputCount: Int = inputCacheDF.count().intValue()
                //训练集取的条数
                val limitCount: Int = Math.round(inputCount * trainingDataRatio).intValue()
                //以后重用，cache
                trainningDataDF = inputCacheDF.orderBy(inputCacheDF(orderedCol).asc).limit(limitCount).cache()
                //如果不按第一列排序，就得求差集，得出测试数据，shuffle量大，不好
                //testDataDF = inputDF.except(trainningDataDF).toDF()

                //测试集取的条数，以后重用，cache
                testDataDF = inputCacheDF.orderBy(inputCacheDF(orderedCol).desc).limit(inputCount - limitCount).cache()
            }
            case _ =>
        }
//        println("训练集条数：" + trainningDataDF.count())
//        println("测试集条数：" + testDataDF.count())
        (trainningDataDF, testDataDF)
    }
}
