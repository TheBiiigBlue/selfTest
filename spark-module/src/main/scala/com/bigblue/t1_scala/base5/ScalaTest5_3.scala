package com.bigblue.t1_scala.base5

/**
  * Created By TheBigBlue on 2018/7/11 : 14:34
  * Description : 偏函数
  */
object ScalaTest5_3{
    /**
      * 被包在花括号内没有match的一组case语句是一个偏函数，它是PartialFunction[A, B]的一个实例
      * A代表输入参数类型，B代表返回结果类型，常用作输入模式匹配
      * 偏函数最大的特点就是它只接受和处理其参数定义域的一个子集
      */
    //偏函数必须指定PartialFunction，并且参数给定输入和输出类型
    def fun1: PartialFunction[String, Int] = {
        case "one" => 1
        case "two" => 2
        case _ => -1
    }
    //fun2是普通方法模拟偏函数
    def fun2(num: String) : Int = num match {
        case "one" => 1
        case "two" => 2
        case _ => -1
    }
    def main(args: Array[String]): Unit = {
        println(fun1("two"))
        println(fun2("one"))
    }
}
