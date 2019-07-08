package com.bigblue.t1_scala.base5

import scala.util.Random

/**
  * Created By TheBigBlue on 2018/7/11 : 14:34
  * Description : 样例类和Option类型
  */
object ScalaTest5_2{
    /**
      * 在Scala中样例类是一种特殊的类，可用于模式匹配。定义形式：
      * case class 类型，是多例的，后面要跟构造参数。
      * case object 类型，是单例的。
      */
    def main(args: Array[String]): Unit = {
        val arr = Array(CheckTimeOut, HeartBeat(1000), SubmitTask("11", "task-1"))
        arr(Random.nextInt(arr.length)) match {
            case SubmitTask(id: String, name: String) => println(s"id: $id, name: $name")
            case HeartBeat(time: Long) => println(s"time: $time")
            case CheckTimeOut => println("CheckTimeOut")
            case _ => println("something else")
        }

        /**
          * 在Scala中Option类型用样例类来表示可能存在或者可能不存在的值(Option的子类有Some和None)。
          * Some包装了某个值，None表示没有值
          */
        val map = Map("a" -> 1, "b" -> 2)
        val v = map.get("c") match {
            case Some(i) => i
            case None => 0
        }
        println(v)
        //也可以
        println(map.getOrElse("c", 0))
    }
}
case class SubmitTask(id: String, name: String)
case class HeartBeat(time: Long)
case object CheckTimeOut