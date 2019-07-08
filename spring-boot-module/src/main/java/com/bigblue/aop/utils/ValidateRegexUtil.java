package com.bigblue.aop.utils;

import com.bigblue.annotation.validate.RegexValidate;
import com.bigblue.aop.vo.FieldInfo;
import com.bigblue.api.JsonResponse;
import com.bigblue.enums.ApiEnum;
import com.bigblue.utils.common.ObjectIsNullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author: TheBigBlue
 * @Description: 校验正则
 * @Date: 2019/6/19
 */
public class ValidateRegexUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateRegexUtil.class);

    /**
     * @Author: TheBigBlue
     * @Description: 校验正则
     * @Date: 2019/6/19
     * @Param anno: 注解对象
     * @Param rqsParams: 请求参数和参数值
     * @Return:
     **/
    public static JsonResponse validateRegex(Annotation anno, Map<String, Object> rqsParams) {
        RegexValidate regexValidateAnno = (RegexValidate) anno;
        //需要校验的字段
        String[] fieldNames = regexValidateAnno.value();
        //正则表达式
        String regex = regexValidateAnno.regex();
        //获取所有需要校验的字段的属性名和属性值
        List<FieldInfo> fieldInfoList = CommonAspectUtil.getFieldInfoList(fieldNames, rqsParams);
        for (FieldInfo fieldInfo : fieldInfoList) {
            //校验每个属性，有一个不符合则返回
            JsonResponse fail = validate(fieldInfo.getFieldName(), fieldInfo.getFieldValue(), regex);
            if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(fail.getCode())) {
                LOGGER.info("正则格式错误!");
                return fail;
            }
        }
        return JsonResponse.success();
    }

    /**
     * @Author: TheBigBlue
     * @Description: 校验正则格式
     * @Date: 2019/6/19
     * @Param fieldName: 属性名称
     * @Param fieldValue: 属性值
     * @Param regex: 正则格式
     * @Return:
     **/
    public static JsonResponse validate(String fieldName, Object fieldValue, String regex) {
        if (ObjectIsNullUtil.isNullOrEmpty(fieldValue)) {
            LOGGER.info(fieldName + "参数为空！");
            return JsonResponse.fail(fieldName + "参数为空！");
        } else {
            //校验正则格式
            try {
                Pattern pattern = Pattern.compile(regex);
                if (!pattern.matcher((String) fieldValue).matches()) {
                    LOGGER.info(fieldName + "正则格式错误。");
                    return JsonResponse.fail(fieldName + "正则格式错误！");
                }
                return JsonResponse.success();
            } catch (Exception e) {
                LOGGER.error(fieldName + "正则格式错误。", e);
                return JsonResponse.fail(fieldName + "正则格式错误！");
            }
        }
    }
}
