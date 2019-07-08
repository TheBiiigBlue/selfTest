package com.bigblue.t1_scala.base7

/**
  * Created By TheBigBlue on 2018/7/12 : 10:30
  * Description : 
  */
object ScalaTest7_2{
    /**
      * 2.Scala中隐式转换Demo2
      * 隐式转换案例二
      */
    //隐式转换方法
    implicit def man2SuperMan(man: Man) = new SuperMan(man.name)
    def main(args: Array[String]): Unit = {
        val man = new Man("tom")
        //Man具备了SuperMan的方法
        man.heat
    }
}
class Man(val name: String)
class SuperMan(val name: String){
    def heat = println("超人打怪兽")
}