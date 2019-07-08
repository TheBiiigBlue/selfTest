package com.bigblue.t1_scala.base2

/**
  * Created By TheBigBlue on 2018/7/9 : 16:59
  * Description : 1.2 Scala的类的构造
  */
object ScalaTest2_1_2{

    def main(args: Array[String]): Unit = {
        val c1 = new ScalaTest2_1_2("zhangsan", 18)
        println("==========================")
        val c2 = new ScalaTest2_1_2("lisi", 20, "female")
    }
}

/**
  * 1.2.Scala的类的构造
  * Scala中的每个类都有主构造器，主构造器的参数直接放置类名后面，与类交织在一起。
  */
class ScalaTest2_1_2(val name: String, var age: Int) {

    //注意：主构造器会执行类定义中的所有语句。
    println("执行主构造器")

    private var gender = "male"
    //辅助构造器,执行辅助构造器的第一句必须执行主构造器，就像java中构造器第一句默认为super一样
    def this(name: String, age: Int, gender: String) {
        this(name, age)
        println("执行辅助构造器")
        this.gender = gender
    }
}
