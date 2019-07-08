package com.bigblue.t1_scala.base2

/**
  * Created By TheBigBlue on 2018/7/9 : 17:24
  * Description : 2.1 Scala的对象
  */
object ScalaTest9_1{
    /**
      * 3.Scala的对象object
      * object 相当于 class 的单个实例，通常在里面放一些静态的 field 或者 method；
      * 在Scala中没有静态方法和静态字段，但是可以使用object这个语法结构来达到同样的目的
      *
      * object作用：
      *  1.存放工具方法和常量
      *  2.高效共享单个不可变的实例
      *  3.单例模式
      */
    def main(args: Array[String]): Unit = {
        //通过单例工厂获取单例对象
        val test1 = TestFactory.getTest()
        println(test1)
        //通过访问单例工厂的属性直接获取单例对象
        val test2 = TestFactory.test
        println(test2)
        //两者皆为一个对象，为单例模式
    }
}
object TestFactory {
    //初始化一个session对象，并且不可改变
    val test = new Test
    //代码块的最后一个表达式即为块的最终结果，即方法的结果，返回一个单例的Session对象
    def getTest() : Test = {
        test
    }
}
class Test {}
