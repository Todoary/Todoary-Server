package com.todoary.ms.src.config.aspect;

import com.todoary.ms.util.BaseResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspectHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Pointcut("execution(* com.todoary.ms.src.web.controller..*(..))")
    private void allControllers() {
    }

    @AfterReturning(value = "allControllers()", returning = "result")
    public void doReturnLogging(JoinPoint joinPoint, Object result) {
        if (result instanceof BaseResponse) {
            log.info("[Return] {} request={} return={}", joinPoint.getSignature().toShortString(), joinPoint.getArgs(), ((BaseResponse<?>) result).getResult());
        } else {
            log.info("[Return] {} request={} return={}", joinPoint.getSignature().toShortString(), joinPoint.getArgs(), result);
        }
    }

    @AfterThrowing(value = "allControllers()", throwing = "exception")
    public void doExceptionLogging(JoinPoint joinPoint, Exception exception) {
        log.error("[Exception] {} request={} message={}", joinPoint.getSignature().toShortString(), joinPoint.getArgs(), exception.getMessage());
    }
}
