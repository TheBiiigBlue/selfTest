package com.bigblue.aop.aspect;

import com.alibaba.fastjson.JSONObject;
import com.bigblue.api.JsonResponse;
import com.bigblue.exception.BusinessException;
import com.bigblue.utils.common.DateUtil;
import com.bigblue.utils.common.ObjectIsNullUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author: TheBigBlue
 * @Description: 拦截controller，输出入参、响应内容和响应时间
 * @Date: 2019/6/17
 */
@Component
@Aspect
@Order(1001)
public class ParamOutAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamOutAspect.class);
    //对包下所有的controller结尾的类的所有方法增强
    private final String executeExpr = "execution(* com.caxs.app..*Controller.*(..))";

    @Value("${spring.aop.maxReduceTime}")
    private long maxReduceTime;

    /**
     * @Author: TheBigBlue
     * @Description: 环绕通知，拦截controller，输出请求参数、响应内容和响应时间
     * @Date: 2019/6/20
     * @Param joinPoint:
     * @Return:
     **/
    @Around(executeExpr)
    public Object processLog(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取方法名称
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        //获取参数名称
        LocalVariableTableParameterNameDiscoverer paramNames = new LocalVariableTableParameterNameDiscoverer();
        String[] params = paramNames.getParameterNames(method);
        //获取参数
        Object[] args = joinPoint.getArgs();
        //过滤掉request和response,不能序列化
        List<Object> filteredArgs = Arrays.stream(args)
                .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
                //空值转为空串，防止后面get报错
                .map(arg -> arg == null ? "" : arg)
                .collect(Collectors.toList());
        JSONObject rqsJson = new JSONObject();
        rqsJson.put("rqsMethod", methodName);
        rqsJson.put("rqsTime", DateUtil.getCurrentFormatDateLong19());
        if (!ObjectIsNullUtil.isNullOrEmpty(filteredArgs)) {
            //拼接请求参数
            Map<String, Object> rqsParams = IntStream.range(0, filteredArgs.size())
                    .boxed()
                    .collect(Collectors.toMap(j -> params[j], j -> filteredArgs.get(j)));
            rqsJson.put("rqsParams", rqsParams);
        }
        LOGGER.info("Controller请求信息为：" + rqsJson.toJSONString());
        Object resObj = null;
        long startTime = System.currentTimeMillis();
        try {
            //执行原方法
            resObj = joinPoint.proceed(args);
        } catch (Throwable e) {
            LOGGER.error(methodName + "方法执行异常!", e);
            throw new BusinessException(methodName + "方法执行异常!");
        }
        long endTime = System.currentTimeMillis();
        // 打印耗时的信息
        this.printExecTime(methodName, startTime, endTime);
        if (resObj != null) {
            if (resObj instanceof JsonResponse) {
                //输出响应信息
                JsonResponse resJson = (JsonResponse) resObj;
                LOGGER.info(methodName + "响应信息为：" + resJson.toString());
                return resJson;
            } else {
                return resObj;
            }
        } else {
            return JsonResponse.success();
        }
    }

    /**
     * @Author: TheBigBlue
     * @Description: 打印方法执行耗时的信息，如果超过了一定的时间，才打印
     * @Date: 2019/6/20
     * @Param methodName:
     * @Param startTime:
     * @Param endTime:
     * @Return:
     **/
    private void printExecTime(String methodName, long startTime, long endTime) {
        long diffTime = endTime - startTime;
        if (diffTime > maxReduceTime) {
            LOGGER.info(methodName + " 方法执行耗时：" + diffTime + " ms");
        }
        /**
         *TODO 可以集成redis，将每个controller的执行时间追加到redis中，
         * 再用定时每周一次同步到库中，统计每个controller耗时，针对性优化具体逻辑
         */
    }
}
