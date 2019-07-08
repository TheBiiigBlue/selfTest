package com.bigblue.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: TheBigBlue
 * @Description: 正则校验
 * @Date: 2019/6/18
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegexValidate {

    /**
     * 需要校验的字段
     */
    String[] value();

    /**
     * 自定义的正则表达式
     */
    String regex();
}
