package com.bigblue.t1_scala.base1

/**
  * Created By TheBigBlue on 2018/7/5 : 15:34
  * Description : 5.Scala的方法和函数
  */
object ScalaTest1_2 {

  //定义一个方法,方法由def开始，m1为方法名，括号内为参数，括号右边是返回值，等号右边为表达式
  def m1 (x:Int, y: Int) : Int = x * y
  //返回值可以不写，scala会自动识别,但是递归方法，必须指定返回值
  def m2 (x: String, y: String) = x + y

  //定义函数
  //定义了一个匿名函数，左边为参数列表，右边是表达式，该函数没由变量引用，即匿名函数，同时没写返回值
  (x: Int, y: Int) => (x + y)

  //普通函数,即有变量引用的函数
  val f1 = (x: String, y: String) => {x + y}
  val f2 = (x: Int) => x * 10
  val f3 = (x: Int) => x + 10

  //将函数传入方法中
  val f4 = (x: Int) => x * 10
  def m3 (f: Int => Int) : Int = {
    //方法中调用传入的函数
    f(3)
  }

  //将方法转化为函数
  def m4 (x : Int, y: Int) : Int = x * y

  def main(args: Array[String]): Unit = {
    //调用方法
    println(m1(2, 4))
    println(m2("Hello ", "World"))

    //调用函数
    println(f1("Hello ", "Scala"))

    //在函数式编程语言中，函数是“头等公民”，它可以像任何其他数据类型一样被传递和操作
    //其实就是把函数f2，f3的功能给传递到方法中，执行函数的功能
    //这里就是把f2的x*10传入map方法，执行了x*10，把f3的x+10传入map方法，执行了x+10
    val r = 1 to 10
    println(r.map(f2))
    println(r.map(f3))

    //函数传入方法
    println("函数传入方法中，结果为：" + m3(f4))

    //三种相同功能的不同写法
    println(r.map((x: Int) => x * 100)) //map中传入一个匿名函数，参数为Int，功能是map取出的每个元素*100
    println(r.map(x => x * 100)) //既然知道传入的必然是Int，则参数可以不写，即：map每个元素赋予x，功能是x*100
    println(r.map(_ * 100)) //再可以直接简写为_

    val a2 = Array(1,2,3,4,5,6)
    //map中传入匿名函数
    val r1 = a2.map(x => x * 10)
    val r2 = a2.map(x => x - 10)
    //这样打印的是地址
    println(r1)
    //scala中用toBuffer查看里面的内容
    println(r1.toBuffer)
    //还是输出的是地址
    println(r1.toString)
    println(r2.toBuffer)
    println(a2.toBuffer)

    //将方法转化为函数，神奇的下划线
    val f5 = m4 _
    println(f5(3, 2))
  }
}
