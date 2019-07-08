package com.bigblue.t1_scala.base7

/**
  * Created By TheBigBlue on 2018/7/12 : 10:42
  * Description : 
  */
object Company {
    //在object中定义隐式值    注意：同一类型的隐式值只允许出现一次，否则会报错
    implicit val name = "zhangsan"
    implicit val money = 1000.0
}
object ScalaTest7_4{
    /**
      * 4.Scala中隐式转换Demo4
      * 隐式参数案例四（员工领取薪水）
      */
    def main(args: Array[String]): Unit = {
        val boss = new Boss
        import Company._
        println(boss.callName() + " "  + boss.getMoney())
    }
}
class Boss {
    //定义一个用implicit修饰的参数
    //注意参数匹配的类型   它需要的是String类型的隐式值
    def callName() (implicit name: String): String = {
        name + " is coming !"
    }
    //注意参数匹配的类型    它需要的是Double类型的隐式值
    def getMoney() (implicit money: Double): String = {
        "当月工资：" + money
    }
}


