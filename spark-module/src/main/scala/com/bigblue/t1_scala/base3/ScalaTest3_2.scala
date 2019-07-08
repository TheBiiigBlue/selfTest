package com.bigblue.t1_scala.base3

/**
  * Created By TheBigBlue on 2018/7/10 : 9:55
  * Description : Scala中的类型判断和类型转换
  */
object ScalaTest3_2{
    /**
      * 可以使用isInstanceOf判断对象是否为指定类的对象，如果是的话，则可以使用 asInstanceOf 将对象转换为指定类型；
      * 如果对象是 null，则 isInstanceOf 一定返回 false， asInstanceOf 一定返回 null
      *
      * isInstanceOf 只能判断出对象是否为指定类以及其子类的对象，而不能精确的判断出，对象就是指定类的对象
      * 如果要求精确地判断出对象就是指定类的对象，那么就只能使用 getClass 和 classOf 了；
      * p.getClass 可以精确地获取对象的类，classOf[XX] 可以精确的获取类，然后使用 == 操作符即可判断；
      */
    def main(args: Array[String]): Unit = {

        val p: Person1 = new Student1
        var s: Student1 = null
        //如果对象是 null，则 isInstanceOf 一定返回 false
        println(s.isInstanceOf[Student1])

        // 判断 p 是否为 Student1 对象的实例
        if(p.isInstanceOf[Student1]) {
            //把 p 转换成 Student1 对象的实例
            s = p.asInstanceOf[Student1]
        }
        println(s.isInstanceOf[Student1])

        //判断p的类型是否为Person1类或者Student1类
        println(p.getClass == classOf[Person1])
        println(p.getClass == classOf[Student1])
        println(p.isInstanceOf[Person1])
    }
}
class  Person1 {}
class Student1 extends Person1
