package com.bigblue.t1_scala.base1

import scala.collection.mutable.ListBuffer

/**
  * Created By TheBigBlue on 2018/7/9 : 14:27
  * Description : 9.Scala中的List
  */
object ScalaTest1_6{

    def main(args: Array[String]): Unit = {
        /**
          * Scala的集合有三大类：序列Seq、Set、映射Map，所有的集合都扩展自Iterable特质，
          * 在Scala中集合有可变（mutable）和不可变（immutable）两种类型，
          * immutable类型的集合初始化后就不能改变了（注意与val修饰的变量进行区别）
          */

        /*
          在Scala中列表要么为空（Nil表示空列表）,要么是一个head元素加上一个tail列表
          list常用的操作符：
          +: (elem: A): List[A] 在列表的头部添加一个元素
          :: (x: A): List[A]     在列表的头部添加一个元素
          :+ (elem: A): List[A] 在列表的尾部添加一个元素
          ++[B](that: GenTraversableOnce[B]): List[B] 从列表的尾部添加另外一个列表
          ::: (prefix: List[A]): List[A] 在列表的头部添加另外一个列表*/

        /************************不可变List***********************************/

        //创建一个不可变的集合
        val list1 = List(1,2,3)
        //另一种定义list方法
        val list2 = 2 :: Nil
        println("list2: " + list2)
        //获取集合的第一个元素，head元素
        val first = list1.head
        println("first: " + first)
        //获取tail列表
        val tail = list1.tail
        println("tail: " + tail)
        //通过下标获取元素
        val mid = list1(1)
        println("mid: " + mid)

        //如果 List 中只有一个元素，那么它的 head 就是这个元素，它的 tail 就是 Nil；
        println(list2.head + "-->" + list2.tail)

        //将0插入到list1的前面生成一个新的List
        val list3 = 0 :: list1
        println("list3: " + list3)
        println("list3: " + list1.+:(0))
        val list4 = 0 +: list1
        println("list4: " + list4)
        println("list4: " + list1.+:(0))

        //将一个元素添加到lst1的后面产生一个新的集合
        val list5 = list1 :+ 0
        println("list5: " + list5)
        println("list5: " + list1.:+(0))

        //将2个list合并成一个新的List
        val list6 = List(4,5,6)
        println(list1 ++ list6)
        println(list1.++(list6))

        //将list6插入到list1前面生成一个新的集合
        println(list6 ++ list1)
        println(list1.:::(list6))

        /************************可变List***********************************/

        //构建一个可变列表，初始有3个元素1,2,3
        val blist = ListBuffer[Int](1,2,3)
        //创建一个空的可变列表
        val blist1 = new ListBuffer[Int]

        //向lst1中追加元素，注意：没有生成新的集合
        blist1 += 4
        blist1.append(5)
        blist1.+=(6)
        println("blist: " + blist)
        println("blist1: " + blist1)

        //将blist1中的元素追加到blist中， 注意：没有生成新的集合
        blist ++= blist1
        println("blist: " + blist)

        //将blist和blist1合并成一个新的ListBuffer 注意：生成了一个集合
        val blist2 = blist ++= blist1
        println("blist2: " + blist2)

        //将元素追加到blist的后面生成一个新的集合
        val blist3 = blist :+ 4
        println("blist3: " + blist3)

        //从集合左侧删除元素,注意：没有生成新的集合
        blist3 -= 4
        println("blist3: " + blist3)

        //删除一个集合列表
        blist3 --= List(1,2)
        println("blist3: " + blist3)

        //删除一个集合列表,生成了一个新的集合
        val blist4 = blist3 -- List(6,4)
        println("blist4: " + blist4)

        //把可变list 转换成不可变的list 直接加上toList
        val blist5 = blist4.toList
        println("blist5: " + blist5)
    }
}
