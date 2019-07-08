package com.bigblue.t1_scala.base1

import scala.collection.mutable.HashSet

/**
  * Created By TheBigBlue on 2018/7/9 : 15:11
  * Description : 10.Scala中的Set
  */
object ScalaTest1_7{

    def main(args: Array[String]): Unit = {
        /**
          * (1)不可变的Set    import scala.collection.immutable._
          * Set代表一个没有重复元素的集合；将重复元素加入Set是没有用的，
          * 而且 Set 是不保证插入顺序的，即 Set 中的元素是乱序的。
          * 定义：val set=Set(元素,元素,.....)
          */

        //定义一个不可变的Set集合
        val set = Set(1,2,3,4,5)
        //元素个数
        println("元素个数： " + set.size)
        //取集合最小值
        println("集合最小值： " + set.min)
        //取集合最大值
        println("集合最大值： " + set.max)
        //添加元素，生成新的set，原有set不变
        println(set + 6)

        val set1 = Set(1,2,6,7,8,9)
        //两个集合的交集
        println("交集：" + (set & set1))
        //两个集合的并集
        println("并集：" + (set | set1))
        println("并集：" + (set ++ set1))
        //返回第一个不同于第二个set的元素集合
        println("差集：" + (set -- set1))
        println("差集：" + (set &~ set1))
        println("差集：" + set.diff(set1))
        //计算符合条件的元素个数
        println("符合条件的元素个数：" + set.count(_ > 2))
        //取子set，包含头不包含尾)
        println(set.slice(2, 5))
        //迭代所有的子set，取指定的个数组合
        set.subsets(3).foreach(x => println("当前子set: " + x))

        //(2)可变的Set  import scala.collection.mutable._

        //定义一个可变的Set
        val set2 = new HashSet[Int]()
        //添加元素
        set2.add(2)
        set2.+=(3)
        println("添加元素：" + set2)
        //向集合中添加元素集合
        println("添加集合：" + (set2 ++= Set(3,4,5)))
        //删除一个元素
        set2 -= 5
        set2.remove(4)
        println("删除元素：" + set2)
    }
}
