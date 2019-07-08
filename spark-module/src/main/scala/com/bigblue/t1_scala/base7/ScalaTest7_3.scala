package com.bigblue.t1_scala.base7

/**
  * Created By TheBigBlue on 2018/7/12 : 10:33
  * Description : 
  */
object Test {
    //创建一个类的2个类的隐式转换
    implicit def C2A(c: C) = new A(c)
    implicit def C2B(c: C) = new B(c)
}
object ScalaTest7_3{
    /**
      * 3.Scala中隐式转换Demo3
      * 隐式转换案例三（一个类隐式转换成具有相同方法的多个类）
      */
    def main(args: Array[String]): Unit = {
        val c = new C
        //会将AB类下的所有隐式转换导进来
//        import Test._
        //只导入C类到A类的的隐式转换方法
//        import Test.C2A
        //只导入C类到B类的的隐式转换方法
        import Test.C2B
        //由于A类与B类中都有readBook()，只能导入其中一个，否则调用共同方法时代码报错
        c.readBook
        //C类可以执行B类中的writeBook()
        c.writeBook
    }
}
class A(c: C) {
    def readBook() = println("A说：好书好书")
}
class B(c: C) {
    def readBook() = println("B说：看不懂看不懂")
    def writeBook() = println("B说：不会写不会写")
}
class C

