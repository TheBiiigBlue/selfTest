package com.bigblue.t96_sparkBizAction.caseClass

/**
  * Created By TheBigBlue on 2019/6/28
  * Description : 
  */

/**
  * 用户对象结构
  * @param UserID id
  * @param Gender 性别，M-男，F-女
  * @param Age  年龄
  * @param Occupation 职业
  * @param Zip_code 邮编
  */
case class Users(UserID: String, Gender: String, Age: Int, Occupation: String, Zip_code: String)

/**
  * 电影对象结构
  * @param MovieID  电影id
  * @param Title    电影标题
  * @param Genres   电影类型
  */
case class Movies(MovieID: String, Title: String, Genres: String)

/**
  * 投票对象结构
  * @param UserID  用户ID
  * @param MovieID  电影ID
  * @param Rating   投票
  * @param Timestamp  时间戳
  */
case class Ratings(UserID: String, MovieID: String, Rating: Double, Timestamp: String)