package com.bigblue.api;


import com.bigblue.enums.ApiEnum;

import java.io.Serializable;

/**
 * @Author wugz
 * @Description 接口返回封装实体
 * @Date  2019/1/30 14:13
 * @Param 
 * @return 
 **/
public class JsonResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
	private String code = null;
    private String message = null;
    private Object data = null;

    private JsonResponse(){

    }

    /**
     * @author ：YanZiheng：
     * @Description ：成功状态，无数据
     * @date ：2018-9-27
     */
    public static JsonResponse success(){
        return success(ApiEnum.RSLT_CDE_000000.getName(),null);
    }

    /**
     * @author ：YanZiheng
     * @return ：
     * @Description ：成功状态，有数据
     * @date ：
     */
    public static JsonResponse success(Object data){
        return  success(ApiEnum.RSLT_CDE_000000.getName(),data);
    }

    /**
     * @author ：YanZiheng：
     * @Description ：成功状态，自定义消息，无数据
     * @date ：2018-9-27
     */
    public static JsonResponse success(String message){
        return  success(message,null);
    }

    /**
     * @author ：YanZiheng：
     * @Description ：更新成功，自定义消息，有数据
     * @date ：2018-9-27
     */

    public static JsonResponse success(String message, Object data){
        JsonResponse jsonResponse =   new JsonResponse();
        jsonResponse.setCode(ApiEnum.RSLT_CDE_000000.getCode());
        jsonResponse.setMessage(message);
        jsonResponse.setData(data);
        return  jsonResponse;
    }


    /**
     * @author ：YanZiheng
     * @return ：
     * @Description ：无定义则返回异常错误
     * @date ：
     */
    public static JsonResponse fail(){
        return  fail(ApiEnum.RSLT_CDE_999999);
    }
    /**
     * @author ：YanZiheng：
     * @Description ：异常类错误
     * @date ：2018-10-10
     */
    public static JsonResponse fail(String message){
        return  fail(ApiEnum.RSLT_CDE_999999.getCode(),message,null);
    }
    /**
     * @author ：YanZiheng
     * @return ：
     * @Description ：非异常类错误，枚举类， 无数据
     * @date ：
     */
    public static JsonResponse fail(ApiEnum apiCodeEnum){

        return  fail(apiCodeEnum.getCode(),apiCodeEnum.getName());
    }

    /**
     * @author ：YanZiheng
     * @return ：
     * @Description ：非异常类错误，枚举类，有数据
     * @date ：
     */
    public static JsonResponse fail(ApiEnum apiCodeEnum, Object data){
        return  fail(apiCodeEnum.getCode(),apiCodeEnum.getName(),data);
    }
    /**
     * @author ：YanZiheng
     * @return ：
     * @Description ：非异常类错误，自定义编码、消息，
     * @date ：
     */

    public static JsonResponse fail(String messageCode, String message){
        return  fail(messageCode,message,null);
    }
    /**
     * @author ：YanZiheng
     * @return ：
     * @Description ：非异常类错误，自定义编码、消息，有数据
     * @date ：
     */
    public static JsonResponse fail(String code, String message , Object data){

        JsonResponse jsonResponse =   new JsonResponse();
        jsonResponse.setCode(code);
        jsonResponse.setMessage(message);
        jsonResponse.setData(data);
        return  jsonResponse;
    }

    public static JsonResponse userDefined(String code) {
        JsonResponse jsonResponse =   new JsonResponse();
        jsonResponse.setMessage(code);
        return  jsonResponse;
    }

    /**
     * @Author wugz
     * @Description 自定义码值
     * @Date  2019/2/19 15:36
     * @Param [code, name]
     * @return com.caxs.app.base.api.JsonResponse
     **/
    public static JsonResponse userDefined(String code, String name) {
        JsonResponse jsonResponse =   new JsonResponse();
        jsonResponse.setCode(code);
        jsonResponse.setMessage(name);
        return  jsonResponse;
    }

    public static JsonResponse userDefined(String code, String message , Object data){
        return setValue(code,message,data);
    }

    public static JsonResponse userDefined(ApiEnum apiCodeEnum){
        return  setValue(apiCodeEnum.getCode(),apiCodeEnum.getName(),null);
    }

    public static JsonResponse userDefined(ApiEnum apiCodeEnum, Object data){
        return  setValue(apiCodeEnum.getCode(),apiCodeEnum.getName(),data);
    }

    private static JsonResponse setValue(String code, String message , Object data){
        JsonResponse jsonResponse =   new JsonResponse();
        jsonResponse.setCode(code);
        jsonResponse.setMessage(message);
        if(data != null){
            jsonResponse.setData(data);
        }
        return  jsonResponse;
    }

    public String getCode() {
        return code;
    }

    private void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "JsonResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}


