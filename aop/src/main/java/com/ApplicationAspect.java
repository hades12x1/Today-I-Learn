package com;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class ApplicationAspect {
 
    @Before("execution (* com.Application.*(..))")
    public void allMethods(JoinPoint joinPoint) {
        System.out.println("Before Method name: " + joinPoint.getSignature().getName());
    }

    @After("execution (* com.Application.*(..))")
    public void allMethodsAfter(JoinPoint joinPoint) {
        System.out.println("After Method name: " + joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "execution (* com.Application.*(..))", throwing = "error")
    public void logThrow(JoinPoint joinPoint, Throwable error) {
        System.out.println("exception in method: " + joinPoint.getSignature().getName());
        System.out.println("Exception is: " + error);
    }
}