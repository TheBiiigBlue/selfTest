package com.bigblue.t1_scala.base6

/**
  * Created By TheBigBlue on 2018/7/12 : 12:25
  * Description : 闭包
  */
object ScalaTest6_3{
    def main(args: Array[String]): Unit = {
        val y = 10
        //变量y不处于其有效作用域时,函数还能够对变量进行访问
        val add = (x: Int) => {
            //在add中有两个变量：x和y。其中的一个x是函数的形式参数，
            //在add方法被调用时，x被赋予一个新的值。
            // 然而，y不是形式参数，而是自由变量
            x + y
        }
        println(add(5))
    }
}
