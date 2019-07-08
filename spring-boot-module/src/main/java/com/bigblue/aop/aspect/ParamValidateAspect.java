package com.bigblue.aop.aspect;

import com.bigblue.annotation.validate.DateValidate;
import com.bigblue.annotation.validate.LengthValidate;
import com.bigblue.annotation.validate.ParamValidate;
import com.bigblue.annotation.validate.RegexValidate;
import com.bigblue.aop.utils.ValidateDateUtil;
import com.bigblue.aop.utils.ValidateLengthUtil;
import com.bigblue.aop.utils.ValidateParamUtil;
import com.bigblue.aop.utils.ValidateRegexUtil;
import com.bigblue.api.JsonResponse;
import com.bigblue.enums.ApiEnum;
import com.bigblue.utils.common.ObjectIsNullUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author: TheBigBlue
 * @Description: 参数校验切面
 * @Date: 2019/6/17
 */
@Component
@Aspect
@Order(1002)
public class ParamValidateAspect {

    //对包下所有类的所有方法增强
    private final String executeExpr = "execution(* com.caxs.app..*.*(..))";

    /**
     * @Author: TheBigBlue
     * @Description: 拦截请求，校验参数
     * @Date: 2019/6/20
     * @Param joinPoint:
     * @Return:
     **/
    @Around(executeExpr)
    public Object paramValidate(ProceedingJoinPoint joinPoint) throws Throwable {
        //请求参数值
        Object[] args = joinPoint.getArgs();
        //请求的方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        JsonResponse resp = JsonResponse.success();
        if (!ObjectIsNullUtil.isNullOrEmpty(args)) {
            List<Object> filteredArgs = Arrays.stream(args)
                    //空值转为空串，防止后面get报错
                    .map(arg -> arg == null ? "" : arg)
                    .collect(Collectors.toList());
            //获取参数名称
            LocalVariableTableParameterNameDiscoverer paramNames = new LocalVariableTableParameterNameDiscoverer();
            String[] params = paramNames.getParameterNames(method);
            //拼接请求参数和参数值
            Map<String, Object> rqsParams = IntStream.range(0, filteredArgs.size())
                    .boxed()
                    .collect(Collectors.toMap(j -> params[j], j -> filteredArgs.get(j)));

            Annotation[] annos = method.getDeclaredAnnotations();
            //如果注解不为空，则检查是否有参数校验注解
            if (!ObjectIsNullUtil.isNullOrEmpty(annos)) {
                for (Annotation anno : annos) {
                    if (anno instanceof DateValidate) {
                        //校验日期
                        resp = ValidateDateUtil.validateDate(anno, rqsParams);
                    } else if (anno instanceof LengthValidate) {
                        //校验字段长度
                        resp = ValidateLengthUtil.validateLength(anno, rqsParams);
                    } else if (anno instanceof ParamValidate) {
                        //校验不为空、身份证号、手机号、Email
                        resp = ValidateParamUtil.validateParam(anno, rqsParams);
                    } else if (anno instanceof RegexValidate) {
                        //校验正则
                        resp = ValidateRegexUtil.validateRegex(anno, rqsParams);
                    }
                    //有校验不通过，返回校验失败
                    if (!ApiEnum.RSLT_CDE_000000.getCode().equalsIgnoreCase(resp.getCode())) {
                        return resp;
                    }
                }
            }
        }
        return joinPoint.proceed();
    }

}
