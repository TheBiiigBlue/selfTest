package com.bigblue.t1_scala.base3

/**
  * Created By TheBigBlue on 2018/7/10 : 10:09
  * Description : 模式匹配
  */
object ScalaTest3_3{
    /**
      * spark 源码中，大量的地方使用了模式匹配的语法进行类型的判断，这种方式更加地简洁明了，而且代码的可维护性和可扩展性也非常高
      * 使用模式匹配，功能性上来说，与 isInstanceOf 的作用一样，主要判断是否为该类或其子类的对象即可，不是精准判断
      * 等同于 Java 中的 switch case 语法
      */
    def main(args: Array[String]): Unit = {
        val p = new Student2
        p match {
            // 匹配是否为Person类或其子类对象
            case p: Person2 => println("This is a Person2's Object")
            //模式匹配，当一种匹配到，则不会再往下匹配了，上面的匹配的是Person类或其子类，已经包括了Student2
            case p: Student2 => println("This is a Student2's Object")
            // 匹配所有剩余情况
            case _ => println("Unknown Type")
        }
    }
}
class Person2
class Student2 extends Person2
