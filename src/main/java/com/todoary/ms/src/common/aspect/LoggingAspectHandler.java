package com.todoary.ms.src.common.aspect;

import com.todoary.ms.src.common.response.BaseResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Aspect
public class LoggingAspectHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Pointcut("execution(* com.todoary.ms.src.web.controller..*(..))")
    private void allControllers() {
    }

    @Pointcut("execution(* com.todoary.ms.src.service.alarm.FireBaseCloudMessageService.*(..))")
    private void allInNotificationService() {
    }

    @AfterReturning(value = "allControllers() || allInNotificationService()", returning = "result")
    public void doReturnLogging(JoinPoint joinPoint, Object result) {
        if (result instanceof BaseResponse) {
            log.info("[Return] {} | request={} | return={}", joinPoint.getSignature().toShortString(), joinPoint.getArgs(), ((BaseResponse<?>) result).getResult());
        } else {
            log.info("[Return] {} | request={} | return={}", joinPoint.getSignature().toShortString(), joinPoint.getArgs(), result);
        }
    }

    @AfterThrowing(value = "allControllers() || allInNotificationService()", throwing = "exception")
    public void doExceptionLogging(JoinPoint joinPoint, Exception exception) {
        String ip = Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(attributes -> attributes.getRequest().getHeader("X-Real-IP"))
                .orElse("");
        log.error("[Exception] {} | ip={} | request={} | message={} | stacktrace={}", joinPoint.getSignature().toShortString(), ip, joinPoint.getArgs(), exception.getMessage(), Arrays.stream(exception.getStackTrace()).limit(5).collect(Collectors.toList()));
    }
}
