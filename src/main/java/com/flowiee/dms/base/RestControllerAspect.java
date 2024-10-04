package com.flowiee.dms.base;

import com.flowiee.dms.entity.system.EventLog;
import com.flowiee.dms.repository.system.EventLogRepository;
import com.flowiee.dms.utils.CommonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Enumeration;

@Aspect
@Component
@RequiredArgsConstructor
public class RestControllerAspect {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EventLogRepository eventLogRepository;
    private ThreadLocal<RequestContext> mvRequestContext = ThreadLocal.withInitial(RequestContext::new); // Tạo ThreadLocal để lưu thông tin của request

    @Getter
    @Setter
    public static class RequestContext {
        private long requestId;
        private long startTime;
        private String username;
        private String ip;
    }

    public RequestContext getRequestContext() {
        return mvRequestContext.get();
    }

    @Before("execution(* com.flowiee.dms.controller.*.*.*(..))")
    public void beforeCall(JoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();
        log.info("AOP Before call system controller {} with arguments: {}", joinPoint, Arrays.toString(args));

        //Save request info into db
        Signature signature = joinPoint.getSignature();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        EventLog eventLog = eventLogRepository.save(EventLog.builder()
                .httpMethod(getHttpMethod(attributes))// Lấy tên HTTP method (GET, POST, etc.)
                .processClass(signature.getDeclaringTypeName())
                .processMethod(signature.getName())
                .requestUrl(getRequestUrl(attributes))
                .requestParam(getRequestParam(attributes))
                .requestBody(getRequestBody(joinPoint))
                .createdBy(CommonUtils.getUserPrincipal().getUsername())
                .createdTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()))
                .ipAddress(CommonUtils.getUserPrincipal().getIp())
                .application(CommonUtils.productID)
                .build());

        RequestContext lvRequestContext = mvRequestContext.get();
        lvRequestContext.setRequestId(eventLog.getRequestId());
        lvRequestContext.setStartTime(startTime);
        lvRequestContext.setUsername(CommonUtils.getUserPrincipal().getUsername());
        lvRequestContext.setIp(CommonUtils.getUserPrincipal().getIp());
        mvRequestContext.set(lvRequestContext);
    }

    @After("execution(* com.flowiee.dms.controller.*.*.*(..))")
    public void afterCall(JoinPoint joinPoint) {
        RequestContext lvRequestContext = mvRequestContext.get();
        long duration = System.currentTimeMillis() - lvRequestContext.getStartTime();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        EventLog eventLog = eventLogRepository.find(lvRequestContext.getRequestId(),
                null,//getHttpMethod(attributes)
                null,//getRequestUrl(attributes)
                null);//LocalDateTime.ofInstant(Instant.ofEpochMilli(lvRequestContext.getStartTime()), ZoneId.systemDefault())
        if (eventLog != null) {
            eventLog.setDuration(duration);
            eventLogRepository.save(eventLog);
        }
        mvRequestContext.remove();
    }

    private String getHttpMethod(ServletRequestAttributes attributes) {
        if (attributes != null) {
            return attributes.getRequest().getMethod();
        }
        return null;
    }

    private String getRequestUrl(ServletRequestAttributes attributes) {
        if (attributes != null) {
            HttpServletRequest httpServletRequest = attributes.getRequest();
            return httpServletRequest.getRequestURL().toString();
        }
        return null;
    }

    private String getRequestParam(ServletRequestAttributes attributes) {
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> parameterNames = request.getParameterNames();
            StringBuilder params = new StringBuilder();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                params.append(paramName).append("=").append(paramValue).append(", ");
            }
            String paramsStr = params.toString();
            if (paramsStr.endsWith(", ")) {
                paramsStr = paramsStr.substring(0, paramsStr.length() - 2);
            }
            return paramsStr;
        }
        return null;
    }

    private String getRequestBody(JoinPoint joinPoint) {
        // Lấy ra chữ ký của phương thức (MethodSignature)
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // Lấy danh sách tham số của phương thức
        Object[] args = joinPoint.getArgs();
        // Lấy danh sách các annotation của từng tham số
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        // Loop qua các tham số và kiểm tra xem có annotation @RequestBody không
        for (int i = 0; i < args.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof RequestBody) {
                    return args[i].toString();
                }
            }
        }
        return null;
    }
}