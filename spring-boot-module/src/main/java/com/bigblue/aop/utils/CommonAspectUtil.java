package com.bigblue.aop.utils;

import com.bigblue.aop.vo.FieldInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: TheBigBlue
 * @Description:
 * @Date: 2019/6/19
 */
public class CommonAspectUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonAspectUtil.class);

    /**
     * @Author: TheBigBlue
     * @Description: 根据对象和属性名获取属性值
     * @Date: 2019/6/19
     * @Param targetObj:
     * @Param fileName:
     * @Return:
     **/
    public static Object getFieldByObjectAndFileName(Object targetObj, String fileName) throws Exception {
        Method methdo = targetObj.getClass().getMethod(getGetterNameByFieldName(fileName));
        return methdo.invoke(targetObj);
    }

    /**
     * @Author: TheBigBlue
     * @Description: 根据属性名得到该属性的getter方法名
     * @Date: 2019/6/19
     * @Param fieldName:
     * @Return:
     **/
    public static String getGetterNameByFieldName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * @Author: TheBigBlue
     * @Description: 获取所有需要校验的字段的属性名和属性值
     * @Date: 2019/6/19
     * @Param fieldNames: 需要校验的字段数组
     * @Param rqsParams: 属性名和属性值map
     * @Return:
     **/
    public static List<FieldInfo> getFieldInfoList(String[] fieldNames, Map<String, Object> rqsParams) {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        for (String fieldName : fieldNames) {
            try {
                if (fieldName.indexOf(".") > 0) {
                    //只校验对应对象中的属性
                    String[] split = fieldName.split("\\.");
                    //根据对象和属性名获取属性值
                    Object fieldValue = getFieldByObjectAndFileName(rqsParams.get(split[0]), split[1]);
                    fieldInfoList.add(new FieldInfo(split[1], fieldValue));
                } else if (fieldName.indexOf(":all") > 0) {
                    //TODO 所有对象中属性都校验
                } else {
                    //校验单属性日期格式
                    fieldInfoList.add(new FieldInfo(fieldName, rqsParams.get(fieldName)));
                }
            } catch (Exception e) {
                LOGGER.error("获取" + fieldName + "属性信息错误！", e);
            }
        }
        return fieldInfoList;
    }
}
