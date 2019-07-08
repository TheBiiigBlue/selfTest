package com.bigblue.t1_scala.base6

/**
  * Created By TheBigBlue on 2018/7/12 : 12:10
  * Description : 科里化
  */
object ScalaTest6_2{
    def main(args: Array[String]): Unit = {
        //不适用科里化的加法
        println(oldSum(1, 3))
        //使用科里化加法
        println(curriedSum(1)(2))
        //也可以只传部分参数，返回剩余参数的函数,即返回x+1的函数
        val fun1: Int => Int = curriedSum(1)
        //调用返回的参数，传入剩余需要的参数，即科里化把一个方法分步了
        println(fun1(2))
    }
    //普通方法
    def oldSum(x: Int, y: Int) = x + y
    //科里化定义加法函数，把函数定义为多个参数列表
    def curriedSum(x: Int)(y: Int) = x + y
    //科里化参数也可以分开写
    def curriedSum1(x: Int) = (y: Int) => x + y
    //多个参数都可以
    def curriedSum2(x: Int)(y: Int)(z: Int) = (o: Int) => x + y + z + o
}
