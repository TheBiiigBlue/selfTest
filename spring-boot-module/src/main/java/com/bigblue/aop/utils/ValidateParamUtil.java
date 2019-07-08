package com.bigblue.aop.utils;

import com.bigblue.annotation.validate.ParamValidate;
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
 * @Description: 校验不为空、身份证号、手机号、Email
 * @Date: 2019/6/19
 */
public class ValidateParamUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateParamUtil.class);
    private static final String NOT_NULL = "不为空";
    private static final String ID_NO = "身份证号";
    private static final String PHONE = "手机号";
    private static final String EMAIL = "邮箱";
    private static final String ID_NO_REGEX = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
    private static final String PHONE_REGEX = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
    private static final String EMIAL_REGEX = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

    /**
     * @Author: TheBigBlue
     * @Description: 校验不为空、身份证号、手机号、Email
     * @Date: 2019/6/19
     * @Param anno: 注解对象
     * @Param rqsParams: 请求参数和参数值
     * @Return:
     **/
    public static JsonResponse validateParam(Annotation anno, Map<String, Object> rqsParams) {
        ParamValidate paramValidateAnno = (ParamValidate) anno;
        //需要校验非空的字段
        String[] notNullFieldNames = paramValidateAnno.notNull();
        JsonResponse notNullFail = chooseTyp(notNullFieldNames, rqsParams, NOT_NULL);
        if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(notNullFail.getCode())) {
            return notNullFail;
        }

        //需要校验身份证的字段
        String[] idNoFieldNames = paramValidateAnno.idNo();
        JsonResponse idNoFail = chooseTyp(idNoFieldNames, rqsParams, ID_NO);
        if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(idNoFail.getCode())) {
            return idNoFail;
        }

        //需要校验手机号的字段
        String[] phoneFieldNames = paramValidateAnno.phone();
        JsonResponse phoneFail = chooseTyp(phoneFieldNames, rqsParams, PHONE);
        if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(phoneFail.getCode())) {
            return phoneFail;
        }

        //需要校验Email的字段
        String[] emailFieldNames = paramValidateAnno.email();
        JsonResponse emailFail = chooseTyp(emailFieldNames, rqsParams, EMAIL);
        if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(emailFail.getCode())) {
            return emailFail;
        }

        return JsonResponse.success();
    }

    /**
     * @Author: TheBigBlue
     * @Description: 校验不同类型
     * @Date: 2019/6/19
     * @Param fieldNames: 需要校验的字段
     * @Param rqsParams: 请求参数和参数值
     * @Param typ: 校验类型
     * @Return:
     **/
    public static JsonResponse chooseTyp(String[] fieldNames, Map<String, Object> rqsParams, String typ) {
        if (!ObjectIsNullUtil.isNullOrEmpty(fieldNames)) {
            //获取所有需要校验的字段的属性名和属性值
            List<FieldInfo> fieldInfoList = CommonAspectUtil.getFieldInfoList(fieldNames, rqsParams);
            for (FieldInfo fieldInfo : fieldInfoList) {
                //校验每个属性，有一个不符合则返回
                JsonResponse fail = validate(fieldInfo.getFieldName(), fieldInfo.getFieldValue(), typ);
                if (!fail.getCode().equalsIgnoreCase(ApiEnum.RSLT_CDE_000000.getCode())) {
                    return fail;
                }
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
     * @Param typ: 校验类型
     * @Return:
     **/
    private static JsonResponse validate(String fieldName, Object fieldValue, String typ) {
        if (ObjectIsNullUtil.isNullOrEmpty(fieldValue)) {
            //校验不为空
            LOGGER.info(fieldName + "参数为空！");
            return JsonResponse.fail(fieldName + "参数为空！");
        } else {
            switch (typ) {
                //校验非空，上面已拦截
                case NOT_NULL:
                    return JsonResponse.success();
                //校验身份证号
                case ID_NO:
                    return validateRegex(fieldName, fieldValue, ID_NO, ID_NO_REGEX);
                //校验手机号
                case PHONE:
                    return validateRegex(fieldName, fieldValue, PHONE, PHONE_REGEX);
                //校验Email
                case EMAIL:
                    return validateRegex(fieldName, fieldValue, EMAIL, EMIAL_REGEX);
                //其他情况暂不支持
                default:
                    return JsonResponse.fail("不支持的校验类型！");
            }
        }
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
    public static JsonResponse validateRegex(String fieldName, Object fieldValue, String typ, String regex) {
        //校验日期格式
        try {
            Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher((String) fieldValue).matches()) {
                LOGGER.info(fieldName + typ + "格式错误。");
                return JsonResponse.fail(fieldName + typ + "格式错误。");
            }
            return JsonResponse.success();
        } catch (Exception e) {
            LOGGER.error(fieldName + typ + "格式错误。", e);
            return JsonResponse.fail(fieldName + typ + "格式错误。");
        }
    }

}
