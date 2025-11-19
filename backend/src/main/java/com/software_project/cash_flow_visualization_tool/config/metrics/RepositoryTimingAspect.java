package com.software_project.cash_flow_visualization_tool.config.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryTimingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryTimingAspect.class);

    @Around("execution(* com.software_project.cash_flow_visualization_tool..*(..))")
    public Object logRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        logger.info("Execution time for {} : {} ms", joinPoint.getSignature(), (end - start));

        return result;
    }
}