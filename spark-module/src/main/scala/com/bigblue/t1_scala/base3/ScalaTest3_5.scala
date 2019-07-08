package com.bigblue.t1_scala.base3

/**
  * Created By TheBigBlue on 2018/7/10 : 10:23
  * Description : 调用父类构造
  */
object ScalaTest3_5{
    /**
      * Scala中，每个类都可以有一个主constructor和任意多个辅助constructor，
      * 而且每个辅助constructor的第一行都必须调用其他辅助constructor或者主constructor代码；
      * 因此子类的辅助constructor是一定不可能直接调用父类的constructor的
      * 只能在子类的主constructor中调用父类的constructor
      */
    def main(args: Array[String]): Unit = {
        val s = new Student4("zhangsan", 200.0)
    }
}
class Person4(val name: String, val age: Int) {

    var score: Double = 0.0
    var address: String = "beijing"
    println(s"主构造：name:$name, age:$age, score:$score, address:$address")

    def this (name: String, score: Double) {
        this(name, 30)
        this.score = score
        println(s"辅助构造：name:$name, age:$age, score:$score, address:$address")
    }
}
class Student4(name: String, score: Double) extends Person4(name, score)