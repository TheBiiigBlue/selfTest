package com.bigblue.t1_scala.base1

/**
  * Created By TheBigBlue on 2018/7/9 : 14:16
  * Description : 8.Scala中的元组
  */
object ScalaTest1_5{

    def main(args: Array[String]): Unit = {
        /**
          * 映射是K/V对偶的集合，对偶是元组的最简单形式，元组可以装着多个不同类型的值
          * （1）元组是不同类型的值的聚集；对偶是最简单的元组。
          * （2）元组表示通过将不同的值用小括号括起来，即表示元组
          * 创建元组格式：val tuple=(元素,元素...)
          */
        val t = ("hadoop", "storm", "spark")
        println(t)

        val t1 = ("hadoop", 3.14, 10, true)
        println(t1)

        /**
          * (1) 获取元组中的值格式：
          * 使用下划线加脚标 ，例如 t._1  t._2  t._3
          * 注意：元组中的元素脚标是从1开始的
          */
        println(t1._3)

        /**
          * 将对偶的集合转换成映射：
          * 调用其toMap方法
          */
        val t2 = Array(("tom", 20), ("jerry", 18))
        println(t2.toMap)

        /**
          * 拉链操作
          * 1.使用zip命令可以将多个值绑定在一起
          *     如果两个数组的元素个数不一致，拉链操作后生成的数组的长度为较小的那个数组的元素个数
          * 2.如果其中一个元素的个数比较少，可以使用zipAll用默认的元素填充
          */
        val t3 = Array("tom", "jerry", "hello")
        val t4 = Array(18, 19)
        println(t3.zip(t4).toList)

        //拿一个wangwu来凑数，使hello有匹配到值20
        println(t3.zipAll(t4, "wangwu", 20).toList)
    }
}
