package com.bigblue.t1_scala.base4

/**
  * Created By TheBigBlue on 2018/7/11 : 10:21
  * Description : Trait继承class
  */
object ScalaTest4_5{
    /**
      * 在Scala中trait 也可以继承 class，此时这个 class 就会成为所有继承该 trait 的子类的超级父类
      */
    def main(args: Array[String]): Unit = {
        val p = new Person_Two("tom")
        p.sayHello
    }
}
class MyUtil {
    def printMsg(msg: String) = println(msg)
}
trait Logger_Two extends MyUtil {
    def log(msg: String) = this.printMsg("log: " + msg)
}
class Person_Two(val name: String) extends Logger_Two {
    def sayHello: Unit = {
        this.log("Hello " + this.name)
        this.printMsg("Hello " + this.name + ", I'm super class")
    }
}