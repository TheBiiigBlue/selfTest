package com.bigblue.t1_scala.base2

/**
  * Created By TheBigBlue on 2018/7/10 : 9:15
  * Description : 2.2 伴生对象
  */
//伴生对象
object ScalaTest2_2_2{
    /**
      *  如果有一个class文件，还有一个与class同名的object文件，那么就称这个object是class的伴生对象，class是object的伴生类；
      *  伴生类和伴生对象必须存放在一个.scala文件中；
      *  伴生类和伴生对象的最大特点是，可以相互访问；
      */

    //伴生对象中的私有属性
    private val CONSTANT = "ABC"

    def main(args: Array[String]): Unit = {
        val a = new ScalaTest2_2_2
        //访问伴生类的私有字段name
        a.name = "list"
        a.printName()
    }

}
//伴生类
class ScalaTest2_2_2 {

    val id = 1
    private var name = "zhangsan"

    def printName(): Unit = {
        //在伴生类中可以访问伴生对象的私有属性
        println(ScalaTest2_2_2.CONSTANT + " " + name)
    }
}
