package com.bigblue.t97_utils

import org.apache.spark.sql.DataFrame

/**
  * Created By TheBigBlue on 2019/1/4
  * Description : 
  */
object NullValueCheck {

  /**
   * @Author: TheBigBlue
   * @Description: 校验dataframe中相应的字段数据中是否有空值情况
   * @Date: 2019/1/4
   * @param dataFrame: 输入的dataframe
   * @param colsArr: 需要校验的字段数组
   * @return: long 为空的数据条数
   **/
  def countNullValue(dataFrame: DataFrame, colsArr: Array[String]): Long = {
    var nullValueCount: Long = 0
    if(dataFrame != null && colsArr != null){
      val condition = colsArr.map(colName => "`" + colName + "` is null").mkString(" or ")
      nullValueCount = dataFrame.filter(condition).count()
    }
    nullValueCount
  }
}
