package com.bigblue.t1_scala.base1

import scala.collection.mutable.ArrayBuffer

/**
  * Created By TheBigBlue on 2018/7/5 : 18:06
  * Description : 6.Scala的数组
  */
object ScalaTest1_3{

    def main(args: Array[String]): Unit = {

        //初始化一个长度为8的定长数组
        val arr1 = new Array[Int](8)
        //直接打印定长数组，内容为数组的hashcode值
        println(arr1)
        //将数组转换成数组缓冲，就可以看到原数组中的内容了,toBuffer会将数组转换长数组缓冲
        println(arr1.toBuffer)

        //初始化一个定长数组，自己指定值
        //这里没用new，为自己赋值，这里是长度为1的数组，值为10
        val arr2 = Array[Int](10)
        println(arr2.toBuffer)

        //定义一个长度为3的定长数组
        val arr3 = Array[String]("Hello", "World", "Scala")
        //使用下标来访问数组中元素
        println(arr3(1))

        /********************变长数组 *****************************/

        //变长数组（数组缓冲）
        //如果想使用数组缓冲，需要导入import scala.collection.mutable.ArrayBuffer包
        val array4 = ArrayBuffer[Int]()
        //+=向数组缓冲的尾部追加一个元素
        array4 += 1
        println(array4)

        //尾部追加多个元素
        array4 += (2, 3, 4, 5)
        println(array4)

        //追加一个数组,追加数组和数组缓冲需要用 ++=
        array4 ++= Array(6, 7)
        println(array4)

        //追加一个数组缓冲，需要用++=
        array4 ++= ArrayBuffer(8, 9)
        println(array4)

        array4.insert(0, -1, 0)
        println(array4)

        //删除数组某个位置的多个元素用remove
        //从第9个位置删除两个元素
        array4.remove(9, 2)
        println(array4)

        /***********************数组的遍历 *****************************/

        val array5 = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
        //增强for输出
        for (i <- array5) print(i + ",")
        println()

        //until循环赋值
        for (i <- (0 until array5.length)) print(array5(i) + ",")
        println()

        /**********************数组的转换 *****************************/

        //将原数组的每个元素乘以10放入一个新的数组，yield把每个元素做对应操作后放入一个新的数组
        val res = for (i <- array5) yield i * 10
        println(res.toBuffer)

        //取原数组元素中的偶数+10放入一个新的数组，在for中加if过滤
        val res1 = for(i <- array5 if i % 2 == 0) yield i * 10
        println(res1.toBuffer)

        //更高级的写法,filter过滤，接收一个返回值为boolean的函数
        //_下划线表示每次取出的元素
        //map相当于将数组中的每一个元素取出来，应用传进去的函数
        val res2 = array5.filter(_ % 2 == 0).map(_ * 10)
        println(res2.toBuffer)

        /*********************数组常用算法*****************************/

        //求和
        println(array5.sum)
        //求最大值
        println(array5.max)
        //数组排序，正序
        println(array5.sorted.toBuffer)
        //数组排序，倒序
        println(array5.sorted.reverse.toBuffer)
    }
}
