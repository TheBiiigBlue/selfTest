package com.bigblue.t1_scala.base3

/**
  * Created By TheBigBlue on 2018/7/10 : 10:38
  * Description : 抽象类和抽象Field
  */
object ScalaTest3_7{
    /**
      * 如果在父类中，有某些方法无法立即实现，而需要依赖不同的子类来覆盖，重写实现不同的方法，
      * 此时可以将父类中的这些方法编写成只含有方法签名，不含方法体的形式，这种形式就叫做抽象方法
      * 一个类中，如果含有一个抽象方法或抽象field，就必须使用abstract将类声明为抽象类，该类是不可以被实例化的
      * 在子类中覆盖抽象类的抽象方法时，可以不加override关键字
      */
    def main(args: Array[String]): Unit = {
        val s = new Student5("tom")
        println(s.sayHello)
        print(s.age)
    }
}
//如果在父类中，定义了field，但是没有给出初始值，则此field为抽象field
abstract class Person5(val name: String) {
    //抽象fields
    val age: Int
    //必须指出返回类型，不然默认返回为Unit
    def sayHello: String
}
class Student5(name: String) extends Person5(name: String) {
    //在子类中实现父类的抽象Field
    val age: Int = 50
    //必须指出返回类型，不然默认
    def sayHello: String = "Hello " + name
}
