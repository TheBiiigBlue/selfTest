package com.bigblue.t1_scala.base5

/**
  * Created By TheBigBlue on 2018/7/12 : 10:07
  * Description : 协变，逆变和非变
  */
object ScalaTest5_4{
/*
  协变和逆变主要是用来解决参数化类型的泛化问题。Scala的协变与逆变是非常有特色的，完全解决了Java中泛型的一大缺憾；
  举例来说，Java中，如果有 A是 B的子类，但 Card[A] 却不是 Card[B] 的子类；而 Scala 中，只要灵活使用协变与逆变，就可以解决此类 Java 泛型问题；
  由于参数化类型的参数（参数类型）是可变的，当两个参数化类型的参数是继承关系（可泛化），
  那被参数化的类型在Java中是不可泛化的，然而Scala提供了三个选择，即协变(“+”)、逆变（“-”）和非变。
  首先假设有参数化特征Queue，那它可以有如下三种定义。
  (1)  trait Queue[T] {}
  这是非变情况。这种情况下，当类型B是类型A的子类型，则Queue[B]与Queue[A]没有任何从属关系，这种情况是和Java一样的。
  (2)  trait Queue[+T] {}
  这是协变情况。这种情况下，当类型B是类型A的子类型，则Queue[B]也可以认为是Queue[A]的子类型，即Queue[B]可以泛化为Queue[A]。
  也就是被参数化类型的泛化方向与参数类型的方向是一致的，所以称为协变。
  (3)   trait Queue[-T] {}
  这是逆变情况。这种情况下，当类型B是类型A的子类型，则Queue[A]反过来可以认为是Queue[B]的子类型。
  也就是被参数化类型的泛化方向与参数类型的方向是相反的，所以称为逆变。

  协变、逆变、非变总结
  	C[+T]：如果A是B的子类，那么C[A]是C[B]的子类。
  	C[-T]：如果A是B的子类，那么C[B]是C[A]的子类。
  	C[T]： 无论A和B是什么关系，C[A]和C[B]没有从属关系
*/
    def main(args: Array[String]): Unit = {
        //支持协变 Temp1[Sub]还是Temp1[Super]的子类
        val t1: Temp1[Super] = new Temp1[Sub]("Hello World")
        println(t1.toString)

        //支持逆变 Temp1[Super]是Temp1[Sub]的子类
        val t2: Temp2[Sub] = new Temp2[Super]("Hello World")
        println(t2.toString)

        //支持非变 Temp3[Super]与Temp3[Sub]没有从属关系，如下代码会报错
//        val t3: Temp3[Super] = new Temp3[Sub]("Hello World")
//        val t4: Temp3[Sub] = new Temp3[Super]("Hello World")
    }
}
class Super
class Sub extends Super
//协变
class Temp1[+A](title: String)
//逆变
class Temp2[-A](title: String)
//非变
class Temp3[A](title: String)