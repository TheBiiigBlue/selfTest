package com.bigblue.exception;

/**
 * 
  * @ClassName(类名)      : BusinessException
  * @Description(描述)    : 业务异常
  * @author(作者)         ：曹轩
  * @date (开发日期)      ：2016-4-13 下午4:03:55
  *
 */
public class BusinessException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public BusinessException(){};
	
	public BusinessException(String message)
	  {
	    super(message);
	  }

    public BusinessException(Throwable cause) {
	    super(cause);
	  }
	  
    public BusinessException(String message, Throwable cause) {
		    super(message, cause);
	  }
}
