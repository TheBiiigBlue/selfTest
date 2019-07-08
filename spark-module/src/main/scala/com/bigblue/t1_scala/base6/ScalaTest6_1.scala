package com.bigblue.t1_scala.base6

/**
  * Created By TheBigBlue on 2018/7/12 : 11:22
  * Description : 作为值的函数
  */
object ScalaTest6_1{
    def main(args: Array[String]): Unit = {
        /********************作为值的函数**********************/
        val arr1 = Array(1,2,3,4,5)
        //定义一个函数，并将函数赋给变量fun
        val fun = (x: Int) => x * 2
        //将函数作为参数传入方法中
        val arr2: Array[Int] = arr1.map(fun)
        println(arr2.toBuffer)

        /********************匿名函数**********************/
        //没有将函数赋给变量的函数叫做匿名函数
        arr1.map((x: Int) => x * 3)
        //由于Scala可以自动推断出参数的类型，所有可以写的跟精简一些
        arr1.map(x => x * 3)
        //神奇的下划线
        arr1.map(_ * 3)
    }
}
