package com.bigblue.t1_scala.base4

/**
  * Created By TheBigBlue on 2018/7/10 : 14:08
  * Description : trait
  */
object ScalaTest4_1{
    /**
      *	Scala中的trait是一种特殊的概念
      * 首先先将trait作为接口使用，此时的trait就与Java中的接口 (interface)非常类似
      * 在trait中可以定义抽象方法，就像抽象类中的抽象方法一样，只要不给出方法的方法体即可
      * 在Scala中没有 implement 的概念，无论继承类还是trait，统一都是 extends
      * 类继承后，必须实现其中的抽象方法，实现时，不需要使用 override 关键字
      * Scala不支持对类进行多继承，但是支持多重继承 trait，使用 with 关键字即可
      */
    def main(args: Array[String]): Unit = {
        val c1 = new Children("tom")
        val c2 = new Children("jerry")
        c1.sayHello
        c1.mkFriends(c2)
    }
}
trait Hello {
    def sayHello
}
trait Friends {
    def mkFriends(c: Children)
}
//多重继承
class Children(val name: String) extends Hello with Friends with Cloneable with Serializable {
    def sayHello:Unit = println("Hello " + this.name)
    def mkFriends(c:Children): Unit = println("My name is " + this.name + " and your name is " + c.name)
}
