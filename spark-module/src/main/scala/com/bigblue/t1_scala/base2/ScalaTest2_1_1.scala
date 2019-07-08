package com.bigblue.t1_scala.base2

/**
  * Created By TheBigBlue on 2018/7/9 : 16:21
  * Description : 1.1 Scala的类
  */
object ScalaTest2_1_1{

    /**
      * 在Scala中，类并不用声明为public类型的。
      * Scala源文件中可以包含多个类，所有这些类都具有共有可见性。
      */
    def main(args: Array[String]): Unit = {
        //和Java中有些不一样，如果是无参构造，则不用括号，加上也没错
        val c = new ScalaTest2_1_1

        //val类型的不支持重新赋值，但是可以获取到值
//        c.id = "123"
        println("id: " + c.id)
        //var 可获取，可修改
        c.age = 20
        println("age: " + c.age)
        //打印name,伴生对象中可以在访问private变量
        c.name = "list"
        println("name: " + c.name)
        //由于pet字段用private[this]修饰，伴生对象中访问不到pet变量
//        c.pet
    }
}

class ScalaTest2_1_1 {
    //用val修饰的变量是可读属性，有getter但没有setter（相当与Java中用final修饰的变量)
    val id = "9827"
    //用var修饰的变量都既有getter，又有setter
    var age = 18
    //类私有字段，只能在类的内部使用或者伴生对象中访问
    private var name = "zhangsan"
    //类私有字段，访问权限更加严格的，该字段在当前类中被访问
    //在伴生对象里面也不可以访问
    private[this] var pet = "ddd"
}
