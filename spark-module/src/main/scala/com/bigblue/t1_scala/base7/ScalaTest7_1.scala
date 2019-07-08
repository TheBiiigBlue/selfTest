package com.bigblue.t1_scala.base7

import java.io.File
import scala.io.Source

/**
  * Created By TheBigBlue on 2018/7/12 : 10:18
  * Description : 隐式转换
  */
object ScalaTest7_1{
    /**
      * 1.Scala中隐式转换Demo1
      * 隐式转换案例一（让File类具备RichFile类中的read方法）
      */
    //定义隐式转换方法
    implicit def file2RichFile(file: File) = new RichFile(file)
}
class RichFile(val f: File){
    def read(): String = Source.fromFile(f).mkString
}
object RichFile {
    def main(args: Array[String]): Unit = {
        val file = new File("")
        //使用import导入隐式转换方法
        import ScalaTest7_1._
        //通过隐式转换，让File类具备了RichFile类中的方法
        val content = file.read()
        println(content)
    }
}
