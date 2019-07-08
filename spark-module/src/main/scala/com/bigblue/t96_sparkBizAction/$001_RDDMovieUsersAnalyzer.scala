package com.bigblue.t96_sparkBizAction

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created By TheBigBlue on 2019/6/28
  * Description : 1.1.1 RDD电影分析代码
  */
object $001_RDDMovieUsersAnalyzer {

  val filePath = "file:///D:/Download/PDF/Spark大数据商业实战/ml-1m/";
  
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("RDD_MOVIE_USERS_ANALYZER")
    val spark = SparkSession.builder().config(conf).getOrCreate()

    val sc = spark.sparkContext
    sc.setLogLevel("WARN")

    val usersRDD = sc.textFile(filePath + "users.dat")
    val moviesRDD = sc.textFile(filePath + "movies.dat")
    val ratingsRDD = sc.textFile(filePath + "ratings.dat")

    /**
      * 业务处理逻辑
      */

    spark.stop()
  }
}
