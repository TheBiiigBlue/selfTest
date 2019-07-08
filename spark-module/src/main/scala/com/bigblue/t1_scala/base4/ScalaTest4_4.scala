package com.bigblue.t1_scala.base4

/**
  * Created By TheBigBlue on 2018/7/11 : 9:55
  * Description : Trait的构造机制
  */
object ScalaTest4_4{
    /**
      * 在Scala中，trait也是有构造代码的，即在trait中，不包含在任何方法中的代码
      * 继承了trait的子类，其构造机制如下：
      *     父类的构造函数先执行， class 类必须放在最左边；多个trait从左向右依次执行
      *     构造trait时，先构造父 trait，如果多个trait继承同一个父trait，则父trait只会构造一次
      *     所有trait构造完毕之后，子类的构造函数最后执行
      */
    def main(args: Array[String]): Unit = {
        new Student_One
    }
}
class Person_One { println("Person_One's Constructor") }
trait Logger_One { println("Logger's Constructor") }
trait MyLogger_One extends Logger_One { println("MyLogger's Constructor") }
trait TimeLogger_One extends Logger_One { println("TimeLogger_One's Constructor") }
class Student_One extends Person_One with MyLogger_One with TimeLogger_One { println("Student's Constructor") }