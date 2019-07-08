package com.bigblue.t1_scala.base5

import scala.util.Random

/**
  * Created By TheBigBlue on 2018/7/11 : 10:27
  * Description : 模式匹配
  */
object ScalaTest5_1{
    /**
      * Scala有一个十分强大的模式匹配机制，可以应用到很多场合：如switch语句、类型检查等
      * 并且Scala还提供了样例类，对模式匹配进行了优化，可以快速进行匹配
      */
    def main(args: Array[String]): Unit = {
        /*******************************匹配字符串*********************************/
        val arr = Array("hadoop", "storm", "spark", "zookeeper")
        val name = arr(Random.nextInt(arr.length))
        name match {
            case "hadoop" => println("大数据分布式存储和计算框架")
            case "zookeeper" => println("大数据分布式协调服务框架")
            case "spark" => println("大数据分布式内存计算框架")
            case _ => println("我不认识你...")
        }
        println()

        /*******************************匹配类型*********************************/
        val arr1 = Array("hello", 1, 2.0, true, ScalaTest5_1)
        val r1 = arr1(Random.nextInt(arr1.length))
        println(r1)
        r1 match {
            case x: Int => print("Int " + x)
            case y: Double if(y >=0 ) => println("Double " + y)
            case z: String => println("String " + z)
            case _ => throw new Exception("not match exception")
        }
        println()

        /*******************************匹配数组*********************************/
        val arr2 = Array(1, 2, 3)
        arr2 match {
            //匹配数组只有三个长度，并且头为1
            case Array(1, x, y) => println(x + ", " + y)
            //匹配数组长度为1，且元素为0
            case Array(0) => println("only 0")
            //匹配数组头为1，长度不限制
            case Array(0, _*) => println("start with 0")
            case _ => println("something else")
        }
        println()

        /*******************************匹配元组*********************************/
        val tuple = (1,2,3)
        tuple match {
            //匹配三个元素的元组，并且第一个元素为1
            case (1, x, y) => println(s"1, $x, $y")
            //匹配三个元素的元组，并且第三个元素为5
            case (_, z, 5) => println(z)
            case _ => println("else")
        }
        println()

        /*******************************匹配集合*********************************/
        /**
          * 在Scala中列表要么为空（Nil表示空列表）要么是一个head元素加上一个tail列表
          * :: 操作符是将给定的头和尾创建一个新的列表,是右结合的
          */
        val list = List(1, 2, -1)
        list match {
            //匹配长度为1的List，并且元素为0
            case 0 :: Nil => println("only 0")
            //匹配长度为2的List
            case x :: y :: Nil => println(s"x: $x, y: $y")
            //匹配头为0，长度不限制的List
            case 0 :: tail => println("start with 0")
            case _ => println("something else")
        }
    }
}
