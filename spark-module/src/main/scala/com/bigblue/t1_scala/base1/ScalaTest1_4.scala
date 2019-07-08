package com.bigblue.t1_scala.base1

import scala.collection.mutable.HashMap

/**
  * Created By TheBigBlue on 2018/7/6 : 13:57
  * Description : 7.Scala的Map
  */
object ScalaTest1_4{

    def main(args: Array[String]): Unit = {
        //在Scala中，有两种Map，一个是immutable包下的Map，该Map中的内容不可变；另一个是mutable包下的Map，该Map中的内容可变
        //(1)不可变的Map   import scala.collection.immutable._
        // 创建map的第一种方式，用箭头
        val map1 = Map("zhangsan" -> 18, "lisi" -> 20)
        println(map1)

        //创建map的第二种方式，用元组
        val map2 = Map(("zhangsan", 20), ("lisi", 18))
        println(map2)

        //获取和修改Map中的值,通过key获取map的value
        println(map2("zhangsan"))
        println(map2.get("lisi"))
        println(map2.get("lisi").get)

        //如果map中没有该key，则会报错，可以使用map.getOrElse(key, defalut)
        //println(map2("wangwu"))
        println(map2.getOrElse("wangwu", "null"))

        //遍历map集合
        //方法一：显示所有的key
        val keys: Iterable[String] = map2.keys
        println(keys)
        for(i <- keys) println(i + "-->" + map2(i))
        println()

        //方法二：显示所有的key
        val set: Set[String] = map2.keySet
        println(set)
        for(i <- set) println(i + "-->" + map2(i))
        println()

        //通过元组
        for((i, j) <- map2) println(i + "-->" + j)
        println()

        //通过模式匹配
        map2.foreach{
            case (x, y) => println(x + "-->" + y)
        }
        println()

        //通常我们在创建一个集合是会用val这个关键字修饰一个变量（相当于java中的final），
        //那么就意味着该变量的引用不可变，该引用中的内容是不是可变，取决于这个引用指向的集合的类型
        //(2)可变的Map  import scala.collection.mutable._
        val user = HashMap("zhangsan" -> 20, "lisi" -> 18)
        println(user)

        //添加键值对
        user += ("wangwu" -> 19)
        println(user)

        //添加多个键值对
        user += (("zhaoliu", 22), "tianqi" -> 23)
        println(user)

        //更新键值对
        user("zhangsan") = 15
        println(user)

        //更新多个键值对
        user += ("zhangsan" -> 33, "lisi" -> 40)
        user.put("wangwu", 40)
        println(user)

        //删除key
        user -= ("zhangsan")
        user.remove("lisi")
        println(user)
    }
}
