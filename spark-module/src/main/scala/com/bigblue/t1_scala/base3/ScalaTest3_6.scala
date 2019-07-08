package com.bigblue.t1_scala.base3

/**
  * Created By TheBigBlue on 2018/7/10 : 10:37
  * Description : Scala的匿名内部类
  */
object ScalaTest3_6{
    /**
      * 在Scala中，匿名内部类是非常常见的，而且功能强大。Spark的源码中大量的使用了匿名内部类
      * 匿名内部类，就是定义一个没有名称的子类，并直接创建其对象，然后将对象的引用赋予一个变量
      * 即匿名内部类的实例化对象。然后将该对象传递给其他函数使用
      */
    def main(args: Array[String]): Unit = {
        val p = new Person("tom")
        val d = new Demo
        d.greeting(p)
    }
}
class Person(val name: String){
    def sayHello = "Hello, I'm " + name
}
class Demo{
    //接受Person参数，并规定Person类只含有一个返回String的sayHello方法
    def greeting (p: Person { def sayHello: String }) = {
        println(p.sayHello)
    }
}
