package com.bigblue.aop.utils;

import com.bigblue.annotation.validate.DateValidate;
import com.bigblue.aop.vo.FieldInfo;
import com.bigblue.api.JsonResponse;
import com.bigblue.enums.ApiEnum;
import com.bigblue.exception.BusinessException;
import com.bigblue.utils.common.ObjectIsNullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: TheBigBlue
 * @Description: 校验日期
 * @Date: 2019/6/19
 */
public class ValidateDateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateDateUtil.class);

    /**
     * @Author: TheBigBlue
     * @Description: 校验日期格式
     * @Date: 2019/6/19
     * @Param anno: 注解对象
     * @Param rqsParams: 请求参数和参数值
     * @Return:
     **/
    public static JsonResponse validateDate(Annotation anno, Map<String, Object> rqsParams) {
        DateValidate dateValidateAnno = (DateValidate) anno;
        //日期格式
        String pattern = dateValidateAnno.pattern();
        //需要校验的字段
        String[] fieldNames = dateValidateAnno.value();
        //获取所有需要校验的字段的属性名和属性值
        List<FieldInfo> fieldInfoList = CommonAspectUtil.getFieldInfoList(fieldNames, rqsParams);
        for (FieldInfo fieldInfo : fieldInfoList) {
            //校验每个属性，有一个不符合则返回
            JsonResponse fail = validate(fieldInfo.getFieldName(), fieldInfo.getFieldValue(), pattern);
            if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(fail.getCode())) {
                return fail;
            }
        }
        return JsonResponse.success();
    }

    /**
     * @Author: TheBigBlue
     * @Description: 校验日期格式
     * @Date: 2019/6/19
     * @Param fieldName: 属性名称
     * @Param fieldValue: 属性值
     * @Param pattern: 日期格式
     * @Return:
     **/
    private static JsonResponse validate(String fieldName, Object fieldValue, String pattern) {
        if (ObjectIsNullUtil.isNullOrEmpty(fieldValue)) {
            LOGGER.info(fieldName + "参数为空！");
            return JsonResponse.fail(fieldName + "参数为空！");
        } else {
            //校验日期格式
            try {
                String dateStr = (String) fieldValue;
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                // 设置lenient为false.否则SimpleDateFormat会比较宽松地验证日期
                format.setLenient(false);
                format.parse((String) fieldValue);
                //保证格式完全匹配
                if (dateStr.length() != pattern.length()) {
                    throw new BusinessException();
                }
                return JsonResponse.success();
            } catch (Exception e) {
                LOGGER.error(fieldName + "日期格式错误。", e);
                return JsonResponse.fail(fieldName + "日期格式错误！");
            }
        }
    }
}
