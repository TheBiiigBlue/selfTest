package com.bigblue.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: TheBigBlue
 * @Description: 校验字段长度
 * @Date: 2019/6/18
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LengthValidate {

    /**
     * 需要校验的字段
     */
    String[] value();

    /**
     * 最小长度
     */
    int minLength() default 1;

    /**
     * 最大长度
     */
    int maxLength() default 30;
}
