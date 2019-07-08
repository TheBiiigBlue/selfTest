package com.bigblue.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: TheBigBlue
 * @Description: 参数校验，校验不为空、身份证号、手机号、Email
 * @Date: 2019/6/18
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamValidate {

    /**
     * 需要校验非空的字段
     */
    String[] notNull() default {};

    /**
     * 需要校验身份证的字段
     */
    String[] idNo() default {};

    /**
     * 需要校验手机号的字段
     */
    String[] phone() default {};

    /**
     * 需要校验邮箱的字段
     */
    String[] email() default {};
}
