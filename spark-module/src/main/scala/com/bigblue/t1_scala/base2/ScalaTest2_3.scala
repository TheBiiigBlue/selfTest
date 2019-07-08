package com.bigblue.t1_scala.base2

/**
  * Created By TheBigBlue on 2018/7/10 : 9:23
  * Description : 2.3 Scala的apply方法
  */
object ScalaTest2_3{
    /**
      * object 中非常重要的一个特殊方法，就是apply方法；
      * apply方法通常是在伴生对象中实现的，其目的是，通过伴生类的构造函数功能，来实现伴生对象的构造函数功能；
      * 通常我们会在类的伴生对象中定义apply方法，当遇到类名(参数1,...参数n)时apply方法会被调用；
      * 在创建伴生对象或伴生类的对象时，通常不会使用new class/class() 的方式，而是直接使用 class()，隐式的调用伴生对象的 apply 方法，这样会让对象创建的更加简洁；
      */

    /**
      *  Array 类的伴生对象中，就实现了可接收变长参数的 apply 方法，
      * 并通过创建一个 Array 类的实例化对象，实现了伴生对象的构造函数功能
      */
    def main(args: Array[String]): Unit = {
        //调用了Array伴生对象的apply方法,赋值
        //arr1是长度为1，值为5的数组
        val arr1 = Array(5)
        println(arr1.toBuffer)

        //new了一个Array对象，长度为5，数组里面包含5个null
        val arr2 = new Array[Int](8)
        println(arr2.toBuffer)
    }
}
