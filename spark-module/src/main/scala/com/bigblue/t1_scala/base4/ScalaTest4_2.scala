package com.bigblue.t1_scala.base4

/**
  * Created By TheBigBlue on 2018/7/10 : 14:30
  * Description : Trait的具体方法、具体Field和抽象Field
  */
object ScalaTest4_2{
    /**
      * Scala中的trait不仅可以定义抽象方法，还可以定义具体的方法，此时 trait 更像是包含了通用方法的工具，可以认为trait还包含了类的功能。
      * 比如 trait 中可以包含很多子类都通用的方法，例如打印日志或其他工具方法等等
      * spark就使用trait定义了通用的日志打印方法
      *
      * Scala 中的 trait 可以定义具体的 field，此时继承 trait 的子类就自动获得了 trait 中定义的 field
      * 但是这种获取 field 的方式与继承 class 的是不同的。 如果是继承 class 获取的 field
      * 实际上还是定义在父类中的；而继承 trait获取的 field，就直接被添加到子类中了
      *
      * Scala中的trait也能定义抽象field， 而trait中的具体方法也能基于抽象field编写
      * 继承trait的类，则必须覆盖抽象field，提供具体的值
      */
    def main(args: Array[String]): Unit = {
        val p1 = new PersonForLogger("tom")
        val p2 = new PersonForLogger("jerry")
        p1.mkFriends(p2)
    }
}
trait Logger {
    //Trait中的具体Field和具体方法
    val age: Int = 40
    def log(message: String): Unit = println(message)
    //抽象field
    val msg: String
}
class PersonForLogger(val name: String) extends Logger {
    //必须覆盖
    val msg = "Hello"
    def mkFriends(other: PersonForLogger) = {
        println(this.msg + other.name + ", My name is " + this.name + ", My age is " + this.age)
        this.log("mkFriends method is invoked with parameter [name=" + other.name + "]")
    }
}
