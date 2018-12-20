package com.demon.spring.custom.aop.aspect;

public interface PointcutAdvisor extends Advisor {

    Pointcut getPointcut();
    
}
