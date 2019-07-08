package com.bigblue.aop.utils;

import com.bigblue.annotation.validate.LengthValidate;
import com.bigblue.aop.vo.FieldInfo;
import com.bigblue.api.JsonResponse;
import com.bigblue.enums.ApiEnum;
import com.bigblue.utils.common.ObjectIsNullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @Author: TheBigBlue
 * @Description: 校验字段长度
 * @Date: 2019/6/19
 */
public class ValidateLengthUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateLengthUtil.class);

    /**
     * @Author: TheBigBlue
     * @Description: 校验字段长度
     * @Date: 2019/6/19
     * @Param anno: 注解对象
     * @Param rqsParams: 请求参数和参数值
     * @Return:
     **/
    public static JsonResponse validateLength(Annotation anno, Map<String, Object> rqsParams) {
        LengthValidate lengthValidateAnno = (LengthValidate) anno;
        //需要校验的字段
        String[] fieldNames = lengthValidateAnno.value();
        //最小长度
        int minLength = lengthValidateAnno.minLength();
        //最大长度
        int maxLength = lengthValidateAnno.maxLength();
        //获取所有需要校验的字段的属性名和属性值
        List<FieldInfo> fieldInfoList = CommonAspectUtil.getFieldInfoList(fieldNames, rqsParams);
        for (FieldInfo fieldInfo : fieldInfoList) {
            //校验每个属性，有一个不符合则返回
            JsonResponse fail = valiedate(fieldInfo.getFieldName(), fieldInfo.getFieldValue(), minLength, maxLength);
            if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(fail.getCode())) {
                return fail;
            }
        }
        return JsonResponse.success();
    }

    /**
     * @Author: TheBigBlue
     * @Description:
     * @Date: 2019/6/19 校验字段长度
     * @Param fieldName: 属性名称
     * @Param fieldValue: 属性值
     * @Param minValue: 最小长度
     * @Param maxValue: 最大长度
     * @Return:
     **/
    private static JsonResponse valiedate(String fieldName, Object fieldValue, int minValue, int maxValue) {
        if (ObjectIsNullUtil.isNullOrEmpty(fieldValue)) {
            LOGGER.info(fieldName + "参数为空！");
            return JsonResponse.fail(fieldName + "参数为空！");
        } else {
            //校验字段长度
            try {
                int length = ((String) fieldValue).length();
                if (length < minValue) {
                    return JsonResponse.fail(fieldName + "参数长度过短，不得少于" + minValue);
                } else if (length > maxValue) {
                    return JsonResponse.fail(fieldName + "参数长度过长，不得大于" + maxValue);
                }
            } catch (Exception e) {
                LOGGER.error(fieldName + "校验字段长度错误。");
                return JsonResponse.fail(fieldName + "校验字段长度错误。");
            }
        }
        return JsonResponse.success();
    }

}
