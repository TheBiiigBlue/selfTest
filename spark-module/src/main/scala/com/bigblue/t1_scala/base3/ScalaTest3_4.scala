package com.bigblue.t1_scala.base3

/**
  * Created By TheBigBlue on 2018/7/10 : 10:14
  * Description : protected关键字
  */
object ScalaTest3_4{
    /**
      * 跟 Java 一样，Scala 中同样可使用 protected 关键字来修饰 field 和 method
      * 在子类中，可直接访问父类的 field 和 method，而不需要使用 super 关键字
      * 还可以使用 protected[this] 关键字， 访问权限的保护范围：只允许在当前子类中访问父类的 field 和 method
      */
    def main(args: Array[String]): Unit = {
        val s = new Student3
        s.sayHello
        s.makeFriends()
        s.sayByeBye
    }
}
class Person3 {
    protected  var name = "tom"
    protected[this] var hobby = "game"
    protected def sayBye = println("bye")
}
class Student3 extends Person3 {
    //父类使用protected 关键字来修饰 field可以直接访问
    def sayHello = println("Hello " + name)
    //父类使用protected 关键字来修饰method可以直接访问
    def sayByeBye = sayBye
    def makeFriends() = {
        println("My Hobby is " + hobby + ", your hobby is unknown")
    }
}
