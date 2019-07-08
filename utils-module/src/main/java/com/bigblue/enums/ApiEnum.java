package com.bigblue.enums;

/***
 * 
  * @ClassName(类名)      : ApiEnum
  * @Description(描述)    : 接口返回码值 相关枚举类
  * @author(作者)         ：吴桂镇
  * @date (开发日期)      ：2018年5月21日 上午10:08:59
  *
 */
public enum ApiEnum {

	/**
	 * 公共返回码值
	 */
	RSLT_CDE_000000("请求成功","000000"),
	RSLT_CDE_999999("其他错误","999999");




	private String name;
	private String code;


	ApiEnum(String name, String code) {
		this.name = name;
		this.code = code;
	}

	/**
	 *
	 * @Description(功能描述)    :  通过code获取名称
	 * @author(作者)             ：  曹轩
	 * @date (开发日期)          :  2016-4-6 上午11:56:46
	 * @exception                :
	 * @param code
	 * @return  String
	 */
	public static String getName(String code){
		for (ApiEnum br : ApiEnum.values()) {
			if(br.getCode().equals(code)){
				return br.getName();
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
