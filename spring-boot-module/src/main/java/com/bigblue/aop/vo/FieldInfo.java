package com.bigblue.aop.vo;

import java.io.Serializable;

/**
 * @Author: TheBigBlue
 * @Description: 属性信息
 * @Date: 2019/6/19
 */
public class FieldInfo implements Serializable {

    private String fieldName;
    private Object fieldValue;

    public FieldInfo() {
    }

    public FieldInfo(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldValue=" + fieldValue +
                '}';
    }
}
