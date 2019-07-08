package com.bigblue.t98_project.t1_artemis.cmpt

import com.bigblue.t98_project.t1_artemis.constant.FeaturesConstant
import org.apache.spark.ml.feature.{Bucketizer, QuantileDiscretizer}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, _}

import scala.collection.mutable.{ArrayBuffer, Buffer, LinkedHashMap, ListBuffer}
import scala.util.control.Breaks._

/**
  * Created By TheBigBlue on 2018/10/24 : 10:29
  * Description : spark分组方法，自己转换版本，传送list，返回Dataset
  */
object WoeCalcCmpt3{

    /**
      * WOE计算组件--配置名称
      */
    //连续列字段数组
    val CON_COLS: String = "conCols"
    //离散列字段数组
    val DIS_COLS: String = "disCols"
    //标签列
    val LABEL_COL: String = "labelCol"
    //分段方法
    val SEG_MODE: String = "segMode"
    //分段数
    val SEGMENTS: String = "segments"

    /**
      * WOE计算组件--配置可选项
      */
    //分段方法--等频，equifrequency
    val SEG_MODE_EF: String = "0"
    //分段方法--等距，equidistant
    val SEG_MODE_ED: String = "1"

    //字段类型--连续型
    val COL_TYPE_CON: String = "0"
    //字段类型--离散型
    val COL_TYPE_DIS: String = "1"

    def main(args: Array[String]): Unit = {
        val warehouseLocate = "hdfs://hcluster/user/hive_remote/warehouse"
        val spark: SparkSession = SparkSession.builder().appName("project-test").master("local[2]")
                .config("spark.sql.warehouse.dir", warehouseLocate)
                .enableHiveSupport().getOrCreate()
        spark.sparkContext.setLogLevel("WARN")
        val originalData: DataFrame = spark.read.option("header", "true")   //第一行作为Schema
                .option("inferSchema", "true")  //推测schema类型
                .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\woe-test\\woe_little_test.csv")
//                .csv("file:///D:\\Cache\\ProgramCache\\TestData\\dataSource\\arthemis\\一手车2018031111.csv")
                .cache()
        //        val originalData: DataFrame = spark.sql("select * from holdbase.feature_test4")
        val (disCols, conCols) = getConAndDisCols(originalData)
        val woeCalcConfigMap: Map[String, Any] = Map(DIS_COLS -> disCols, CON_COLS -> conCols,
            LABEL_COL -> "逾期标志", SEG_MODE -> "0", SEGMENTS -> 10)
        val time1 = System.currentTimeMillis()
        val woeDictDF: Dataset[Row] = woeCalc(spark, originalData, woeCalcConfigMap)
        val time2 = System.currentTimeMillis()
        println("woe calc time: " + (time2 - time1))
        woeDictDF.show(false)
        println("woe show time: " + (System.currentTimeMillis() - time2))

        //模拟woe转换
        val transCols: List[String] = disCols ++ conCols
        val woeTransConfigMap: Map[String, Any] = Map(WoeTransCmpt.TRANS_COLS -> transCols)
        val time7 = System.currentTimeMillis()
        val woeTransDF: DataFrame = WoeTransCmpt.woeTransform2(spark, originalData, woeDictDF, woeTransConfigMap)
        val time8 = System.currentTimeMillis()
        println("woe trans time :  " + (time8 - time7))
        woeTransDF.show(false)
        val time9 = System.currentTimeMillis()
        println("woe trans show time :  " + (time9 - time8))
    }

    def getConAndDisCols(trainningDataDF: DataFrame): (List[String], List[String]) = {
        val dis_cols = new ListBuffer[String]
        val con_cols = new ListBuffer[String]
        trainningDataDF.columns.map(col => {
//            if (FeaturesConstant.all_dis_name.contains(col)) {
            if (FeaturesConstant.test_dis_arr.contains(col)) {
                dis_cols += col
//            } else if (FeaturesConstant.all_con_name.contains(col)) {
            } else if (FeaturesConstant.test_con_arr.contains(col)) {
                con_cols += col
            }
        })
        (dis_cols.toList, con_cols.toList)
    }

    /**
      * Created By TheBigBlue On 2018/10/24
      * Description : 输入DF，输出字典DF
      */
    def woeCalc(spark: SparkSession, inputDF: DataFrame, woeConfigMap: Map[String, Any]): Dataset[Row] = {
        val conColsList: List[String] = woeConfigMap.getOrElse(CON_COLS, null).asInstanceOf[List[String]]
        val disColsList: List[String] = woeConfigMap.getOrElse(DIS_COLS, null).asInstanceOf[List[String]]
        val labelCol: String = woeConfigMap.getOrElse(LABEL_COL, null).asInstanceOf[String]
        val segMode: String = woeConfigMap.getOrElse(SEG_MODE, SEG_MODE_EF).asInstanceOf[String]
        val segments: Int = woeConfigMap.getOrElse(SEGMENTS, 10).asInstanceOf[Int]

        var woeDictDF: Dataset[Row] = null
        segMode match {
            //等频
            case SEG_MODE_EF => {
                //计算分桶
                val conCalcStart: Long = System.currentTimeMillis()
                //连续变量分段计算和转化分段
                val conSegDF: DataFrame = consBound2(spark, conColsList, segments, inputDF)
                val conCalcEnd: Long = System.currentTimeMillis()
                println("连续变量分段转化时间： " + (conCalcEnd - conCalcStart))
                val toDictStart = System.currentTimeMillis()
                //生成woe字典
                woeDictDF = toDict1(spark, conColsList, disColsList, if (conSegDF == null) inputDF else conSegDF, labelCol, segments)
                val toDictEnd = System.currentTimeMillis()
                println("生成WOE字典时间： " + (toDictEnd - toDictStart))
            }
            case SEG_MODE_ED => throw new Exception("等距拆分无python程序参考！")
            case _ => throw new Exception("无此类型的分段方法！")
        }
        woeDictDF
    }

    /**
      * Created By TheBigBlue On 2018/10/26
      * Description : 每个连续字段进行分段，获取分段数据的数组，根据分段对当前列计算
      */
    def consBound2(spark: SparkSession, conColsList: List[String], segments: Int, inputDF: DataFrame): DataFrame = {
        //校验数据合法
        if (conColsList == null || conColsList.length == 0) return null
        val conCols = conColsList.toArray
        val endTime1 = System.currentTimeMillis()
        //将连续变量转为double类型
        val columns: Array[Column] = conCols.map(inputDF(_).cast(DoubleType))
        val fitDF: DataFrame = inputDF.select(columns: _*)
        val endTime2 = System.currentTimeMillis()
        println("spark分组转化Double耗时： " + (endTime2 - endTime1))
        //spark分组
        val bucketizer: Bucketizer = new QuantileDiscretizer().setInputCols(conCols)
                .setOutputCols(conCols.map(_ + "_OUT"))
                .setNumBuckets(segments) //设置分箱数
                .setRelativeError(0.01) //设置precision-控制相对误差
                .fit(fitDF)
        //获取分组信息
        val splitArrs: Array[Array[Double]] = bucketizer.getSplitsArray
        //拼接批量udf表达式的数组
        val colsExprArr = new ArrayBuffer[String]
        colsExprArr += "*"
        //对应字段的分段信息map
        val groupValueMap = new LinkedHashMap[String, Array[Double]]
        conCols.zipWithIndex.foreach { case (conCol, index) => {
            //去掉负无穷的元素，将正无穷改为最大值+十亿
            val buffer: Buffer[Double] = splitArrs(index).toBuffer - Double.NegativeInfinity
            val size = buffer.size
            //将正无穷改为最大值加十亿
            if (buffer(size - 1) == Double.PositiveInfinity) {
                buffer(size - 1) = buffer(size - 2) + 10000000000.00
            }
            groupValueMap.put(conCol, buffer.toArray)

            //拼接批量udf表达式
            colsExprArr += "transDouble(`" + conCol + "`, '" + conCol + "')"
        }
        }
        val endTime3 = System.currentTimeMillis()
        println("spark分组耗时： " + (endTime3 - endTime2))

        //注册自定义函数
        spark.udf.register("transDouble", (x: Double, col: String) => {
            val segArr: Array[Double] = groupValueMap.get(col).get
            var boundNumValue: Double = 0
            breakable {
                for (i <- 0.to(segArr.size - 1)) {
                    if (i == 0 && x <= segArr(0)) {
                        boundNumValue = segArr(0)
                        break
                    } else if (x <= segArr(i) && x > segArr(i - 1)) {
                        boundNumValue = segArr(i)
                        break
                    }
                }
            }
            boundNumValue
        })
        //转化，调用udf，删除之前原列
        val transDF = inputDF.selectExpr(colsExprArr.toArray: _*).drop(conCols: _*)
        //改名
        val newNameArr: Array[String] = transDF.schema.fieldNames.map(colName => {
            if (colName.contains("UDF")) {
                colName.substring(colName.lastIndexOf(",") + 2, colName.lastIndexOf(")"))
            } else {
                colName
            }
        })
        val endTime4 = System.currentTimeMillis()
        println("转化耗时： " + (endTime4 - endTime3))
        transDF.toDF(newNameArr: _*)
    }

    /**
      * Created By TheBigBlue On 2018/11/6
      * Description : 对rdd分组计数并生成DataFrame
      */
    def rddToDF(spark: SparkSession, totalDataRDD: RDD[(String, String)]): DataFrame = {
        val schema = StructType(Seq(
            StructField("col_name", StringType, true),
            StructField("bound_num", StringType, true),
            StructField("count", IntegerType, true))
        )
        //分组计数
        val reduceRDD: RDD[Row] = totalDataRDD.map(x => (x._1, 1)).reduceByKey(_ + _).map(row => {
            val strArr: Array[String] = row._1.split("##")
            Row(strArr(0), strArr(1), row._2)
        })
        spark.createDataFrame(reduceRDD, schema)
    }

    case class WoeDict(col_name: String, bound_num: String, rate: Double, col_type: String)


    /**
      * Created By TheBigBlue On 2018/12/7
      * Description : 传List，判断为空
      */
    def toDict1(spark: SparkSession, conColsList: List[String], disColsList: List[String], originalDF: DataFrame, labelCol: String, segments: Int): Dataset[Row] = {
        val conCols: Buffer[String] = if (conColsList != null && conColsList.length > 0) conColsList.toBuffer else Buffer[String]()
        val disCols: Buffer[String] = if (disColsList != null && disColsList.length > 0) disColsList.toBuffer else Buffer[String]()
        if ((conCols.size == 0 && disCols.size == 0) || labelCol == null) throw new Exception("标签列或离散、连续变量未选择！")
        val originalCols: Array[String] = originalDF.schema.fieldNames
        val disColArr: Buffer[Column] = disCols.filter(originalCols.contains(_)).map(originalDF(_).cast(StringType))
        val conColArr: Buffer[Column] = conCols.filter(originalCols.contains(_)).map(originalDF(_))
        val selectCols: Array[Column] = (disColArr ++ conColArr).+=(new Column(labelCol)).toArray
        val inputDF = originalDF.select(selectCols: _*)
        val selectColsStrArr: Array[String] = inputDF.schema.fieldNames
        //行转列
        val totalDataRDD: RDD[(String, String)] = inputDF.rdd.map(row => {
            val buffer = new ArrayBuffer[(String, String)]()
            selectColsStrArr.foreach(col => {
                val tuple: (String, String) = (col + "##" + row.getAs(col).toString, row.getAs(labelCol).toString)
                buffer += tuple
            })
            buffer.toArray
        }).flatMap(x => x).cache()
        //获取总数据的分组计数DF
        val totalDataDF: DataFrame = rddToDF(spark, totalDataRDD)
        //获取逾期标志为1的分组计数DF
        val posDataDF: DataFrame = rddToDF(spark, totalDataRDD.filter(_._2 == "1")).withColumnRenamed("count", "positive")
        //获取逾期标志为0的分组计数DF
        val negDataDF: DataFrame = rddToDF(spark, totalDataRDD.filter(_._2 == "0")).withColumnRenamed("count", "negative")
        //合并分组计数DF
        val groupedCountDF: DataFrame = totalDataDF.join(posDataDF, Seq("col_name", "bound_num"), "left_outer")
                .join(negDataDF, Seq("col_name", "bound_num"), "left_outer")
                .na.fill(Map[String, Int]("negative" -> 0, "positive" -> 0)).cache()
        import spark.implicits._
        //对positive和negative分别求和
        val sumDF: DataFrame = groupedCountDF.groupBy("col_name").agg(sum("positive").as("pos_sum"), sum("negative").as("neg_sum"))
        val joinDF: DataFrame = groupedCountDF.join(sumDF, Seq("col_name"), "left_outer")
        val bias = 0.000001
        //计算woe并取log
        val woeInfoRateDF: DataFrame = joinDF.select($"col_name", $"bound_num",
            functions.log(((($"positive" + bias) / ($"pos_sum" + bias)) / (($"negative" + bias) / ($"neg_sum" + bias)))).as("rate"))
        //加离散、连续标志列
        val conColsDF: DataFrame = conCols.toList.toDF("con_cols")
        val woeDictDF = woeInfoRateDF.join(conColsDF, woeInfoRateDF("col_name") === conColsDF("con_cols"), "left_outer")
                .select($"col_name", $"bound_num", $"rate", when($"con_cols".isNull, "1").otherwise("0").as("col_type"))
        woeDictDF.orderBy("col_name", "bound_num")
    }
}

