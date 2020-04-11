package com.dream.configs;

import com.dream.exceptions.AuthorizationException;
import com.dream.utils.ErrorCode;
import com.dream.utils.SecurityHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Configuration
public class AuthAspect {

    @Before("methodsAnnotatedWithSecuredAnnotation()")
    public void processMethodsAnnotatedWithSecuredAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Secured securedAnnotation = method.getAnnotation(Secured.class);
    }

    @Pointcut("@annotation(com.dream.configs.Secured)")
    private void methodsAnnotatedWithSecuredAnnotation() {
    }

    @Around("execution(public * *(..)) && @annotation(com.dream.configs.Secured)")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Secured secured = method.getAnnotation(Secured.class);
        if (secured != null) {
            boolean logged = SecurityHelper.isLogged();
            if (!logged) {
                log.warn("secured exception: " + secured);
                throw new AuthorizationException(ErrorCode.SECURED, "Secured");
            }
        }

        Object obj;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            obj = proceedingJoinPoint.proceed();
        } finally {
            stopWatch.stop();
            long time = stopWatch.getTotalTimeMillis();
            log.info("total time in millis for security is: " + time);
        }
        return obj;
    }
}
