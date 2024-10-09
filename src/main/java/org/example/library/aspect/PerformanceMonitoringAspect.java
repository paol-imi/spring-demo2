package org.example.library.aspect;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.library.metrics.PerformanceMetrics;
import org.springframework.stereotype.Component;

/**
 * Aspect for monitoring the performance of Spring components.
 */
@Aspect
@Component
@AllArgsConstructor
public class PerformanceMonitoringAspect {
    /**
     * Performance metrics component.
     */
    private final PerformanceMetrics metrics;

    /**
     * Advice that measures the execution time of methods.
     *
     * @param joinPoint join point for advice
     * @return result of the method execution
     * @throws Throwable if the method execution throws an exception
     */
    @Around("execution(* org.example.library.controller.*.*(..))")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        this.metrics.recordMethodExecutionTime(className, methodName, executionTime);

        return result;
    }
}