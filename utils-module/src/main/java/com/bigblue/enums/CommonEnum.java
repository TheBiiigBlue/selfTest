package com.bigblue.enums;

/***
 * 
  * @ClassName(类名)      : CommonEnum
  * @Description(描述)    : 公共枚举类
  * @author(作者)         ：吴桂镇
  * @date (开发日期)      ：2018年5月14日 上午10:45:10
  *
 */
public enum CommonEnum {

	//redis默认过期时间
	REDIS_EXPIRE_TIME_FEATURE("ATHENA_FEATURE_KEY_PRE的默认过期时间，单位为秒","7200"),
	REDIS_EXPIRE_TIME_WOE("ATHENA_FEATURE_KEY_WOE的默认过期时间，单位为秒","7200");


	private String name;
	private String code;

	private CommonEnum(String name, String code) {
		this.name = name;
		this.code = code;
	}

	/**
	 * 
	 * @Description(功能描述) : 通过code获取名称 @author(作者) ： 曹轩
	 * 
	 * @date (开发日期) : 2016-4-6 上午11:56:46
	 * @exception :
	 * @param code
	 * @return String
	 */
	public static String getName(String code) {
		for (CommonEnum br : CommonEnum.values()) {
			if (br.getCode().equals(code)) {
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

	public String getCode(String code) {
		return code;
	}
}