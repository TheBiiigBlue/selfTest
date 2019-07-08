package com.bigblue.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: TheBigBlue
 * @Description:
 * @Date: 2019/6/18
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateValidate {

    /**
     * 需要校验的字段
     */
    String[] value();

    /**
     * 校验的日期格式
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";
}
