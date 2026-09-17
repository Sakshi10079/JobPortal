package com.example.jobPortal.aspects;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAndPerformanceAspect {

//    @Around("@annotation(com.example.jobPortal.aspects.LogAspect)")
    @Around("execution(* com.example.jobPortal..*.*(..))")
    public Object logAndMeasureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
      long startTime = System.currentTimeMillis();
      String methodName = joinPoint.getSignature().toShortString();
      Object[] methodArgs = joinPoint.getArgs();
      log.info("Entering method: {} ", methodName);
      log.info("Arguments: {} ", methodArgs);

      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - startTime;
      log.info("Method executed successfully: {}", methodName);
      log.info("Execution Time: {}ms ", executionTime);
      return result;
    }
}
