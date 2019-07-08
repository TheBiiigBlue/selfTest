package com.bigblue.t1_scala.base2

/**
  * Created By TheBigBlue on 2018/7/10 : 9:34
  * Description : 2.4 Scala中的main方法
  */
object ScalaTest9_4{
    /**
      * 在 Scala 中，也必须要有一个 main 方法，作为入口；
      * Scala 中的 main 方法定义为 def main(args: Array[String])，而且必须定义在 object 中；
      * 除了自己实现 main 方法之外，还可以继承 App Trait，然后，将需要写在 main 方法中运行的代码，
      * 直接作为 object 的 constructor 代码即可，而且还可以使用 args 接收传入的参数；
      */
    def main(args: Array[String]): Unit = {
        if (args.length > 0) {
            println("hello " + args(0))
        } else {
            println("Hello World")
        }
    }
}
// 使用继承App Trait ,将需要写在 main 方法中运行的代码
// 直接作为 object 的 constructor 代码即可
object MainDemo2 extends App{
    if(args.length > 0){
        println("Hello"+ args(0))
    }else{
        println("Hello World")
    }
}
