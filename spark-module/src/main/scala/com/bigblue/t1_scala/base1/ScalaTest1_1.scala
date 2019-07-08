package com.bigblue.t1_scala.base1

/**
  * Created By TheBigBlue on 2018/7/5 : 14:48
  * Description : 
  */
object ScalaTest1_1 {

  def main(args: Array[String]): Unit = {

    // 1.变量定义
    testVariable()

    // 2.Scala的条件表达式
    testConditionExpression()


    // 3.Scala的块表达式
    testBlockExpression()

    // 4.Scala的for循环
    testForException()

  }

  /**
    * 1. 变量定义
    */
  def testVariable(): Unit = {
    //使用val定义的变量值是不可变的，相当于java里用final修饰的变量
    val i = 1

    //使用var定义的变量是可变得，在Scala中鼓励使用val
    var s = 2

    //Scala编译器会自动推断变量的类型，必要的时候可以指定类型
    //变量名在前，类型在后
    val str: String = "hello scala"
    println(i + "," + s + "," + str)
  }

  /**
    * 2. 条件表达式
    */
  def testConditionExpression(): Unit = {
    val x = 1

    //判断x的值，将结果赋给y
    val y = if (x > 0) 1 else -1
    println("y:" + y)

    //支持混合类型表达式
    val z = if (x > 1) 1 else "error"
    println("z:" + z)

    //如果缺失else，相当于if (x > 2) 1 else ()
    val m = if (x > 1) 1
    println("m:" + m)

    //在scala中每个表达式都有值，scala中有个Unit类，写做(),相当于Java中的void
    val n = if (x > 1) 1 else ()
    println("n:" + n)

    //if和else if
    val k = if (x > 1) 1 else if (x > 0) 0 else -1
    println("k:" + k)
  }

  /**
    * 3.块表达式
    */
  def testBlockExpression(): Unit = {
    val x = 0
    //在scala中{}中课包含一系列表达式，块中最后一个表达式的值就是块的值
    val result = {
      if(x > 0) 1 else if(x == 0) 0 else -1
    }
    //result的值就是块表达式的结果
    println("result: " + result)
  }

  /**
    * 4.for循环
    */
  def testForException(): Unit = {
    //for(i <- 表达式),表达式1 to 10返回一个Range（区间）
    //每次循环将区间中的一个值赋给i
    for (i <- 1 to 10) {
      print(i + ",")
    }
    println()

    //for(i <- 数组)
    val arr = Array("a", "b", "c")
    for (i <- arr) {
      print(i + ",")
    }
    println()

    //高级for循环，每个生成器都可以带一个条件，注意：if前面没有分号
    for (i <- 1 to 3; j <- 1 to 3 if i != j) {
      print(i*10 + j + ",")
    }
    println()

    //for推导式：如果for循环的循环体以yield开始，则该循环会构建出一个集合
    //每次迭代生成集合中的一个值
    val v = for(i <- 1 to 10) yield i * 10
    println(v)

    //上面等同于下面的表达式
    //1.to(10)获得一个1-10的集合，.map()是拿出其中的每一个元素进行操作，操作规则是括号里自己定义的
    //_下滑线是取出的每个元素，_*10相当于一个函数，是取出的每个元素乘以10再组成一个新的集合
    val v1 = 1.to(10).map(_*10)
    println(v1)

    //需求，把 1 to 10 得到的range中的偶数取出来放入一个集合中
    //第一种：for循环中增加条件，只有当i为偶数时，才yield i 返回新的集合
    val a = for(i <- 1 to 10 if (i % 2 == 0)) yield i
    println(a)

    //第二种：使用filter方法，filter过滤，过滤条件是括号内自己定义的函数规则
    //_是每次循环取出的元素，_ % 2 == 0 过滤偶数，即只取偶数
    val b = 1.to(10).filter(_ % 2 == 0)
    println(b)

    //使用数组下标
    val c = Array(1,2,3,4,5,6,7,8,9)
    //不能使用to，to是从前到后，全闭区间，即1-9,所以数组越界了
    //for(i <- 0 to a1.length) print(a1(i) + ",")
    for(i <- 0 to c.length - 1) print(c(i) + ",")
    println()

    //用until，until是从前到后，左闭右开区间，即1-8
    for(i <- 0 until c.length) print(c(i) + ",")
  }

}
