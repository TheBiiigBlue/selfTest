package com.bigblue.t1_scala.base3

/**
  * Created By TheBigBlue on 2018/7/10 : 9:47
  * Description : 1 继承
  */
class ScalaTest3_1{
    /**
      * Scala中，如果子类要覆盖父类中的一个非抽象方法，必须要使用 override 关键字；子类可以覆盖父类的 val 修饰的field，只要在子类中使用 override 关键字即可。
      * override 关键字可以帮助开发者尽早的发现代码中的错误，比如， override 修饰的父类方法的方法名拼写错误。
      * 此外，在子类覆盖父类方法后，如果在子类中要调用父类中被覆盖的方法，则必须要使用 super 关键字，显示的指出要调用的父类方法
      */
    val name = "super"
    def getName = this.name
}

class Student extends ScalaTest3_1 {
    //继承加上关键字
    override
    val name = "sub"
    //或者
//    override val name = "sub"

    //子类可以定义自己的field和method
    val score = "A"

    //覆盖父类非抽象方法，必须要使用 override 关键字
    //同时调用父类的方法，使用super关键字
    override def getName: String = super.getName
}
