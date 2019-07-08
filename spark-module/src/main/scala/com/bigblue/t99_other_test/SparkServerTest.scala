package com.bigblue.t99_other_test

import java.net.InetSocketAddress

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.spark.deploy.SparkSubmit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.spark_project.jetty.server.handler.AbstractHandler
import org.spark_project.jetty.server.{Request, Server}
import org.spark_project.jetty.servlet.ServletContextHandler

/**
  * Created By TheBigBlue on 2018/7/30 : 15:08
  * Description : 
  */
object SparkServerTest {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("project-test").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    //        testConnect(12345)
    testJavaSubmit()
  }

  def testConnect(currentPort: Int): Unit = {
    val server = new Server(new InetSocketAddress("192.168.23.133", currentPort))
    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/hello"); //设置访问路径
    context.setHandler(new SparkServerTest())
    server.setHandler(context)
    server.start()
    server.join()
  }

  def testJavaSubmit(): Unit = {
    System.setProperty("user.name", "lenovo-PC")
    val arg0: Array[String] = Array[String]("--master", "spark://node03:7077",
      "--class", "com.bigblue.spark.core.Test1_1",
      "--executor-memory", "512M",
      "--total-executor-cores", "2",
      "C:/Users/lenovo/Desktop/spark-demo-1.0-SNAPSHOT.jar",
      "D:/Cache/ProgramCache/TestData/dataSource/wordcount",
      //            "file:///root/soft/spark-demo-1.0-SNAPSHOT.jar",
      //             "file:///root/soft/test-file/words.txt",
      "D:/Cache/ProgramCache/TestData/result/java-submit")
    SparkSubmit.main(arg0)
  }
}

class SparkServerTest extends AbstractHandler {
  override
  def handle(s: String, request: Request, httpServletRequest: HttpServletRequest, response: HttpServletResponse): Unit = {
    import java.io.PrintWriter
    response.setContentType("text/html; charset=utf-8")
    response.setStatus(HttpServletResponse.SC_OK)
    val out: PrintWriter = response.getWriter
    val spark: SparkSession = SparkSession.builder().appName("HelloHandler").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val inpath = "hdfs://node01:9000/test/spark/words.txt"
    val outpath = "hdfs://node01:9000/test/result/word-count"
    spark.sparkContext.textFile(inpath).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).saveAsTextFile(outpath)

    val textRDD: RDD[String] = spark.sparkContext.textFile(outpath + "/*")
    val strings: Array[String] = textRDD.collect()
    for (str <- strings) {
      out.println("<h1>" + str + "</h1>")
    }

    request.setHandled(true)
  }
}
