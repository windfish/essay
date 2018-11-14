package com.demon.java8.spring.aop.annotation;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogInterceptor {

    @Before(value="execution(* com.demon.java8.spring.aop.annotation.*.*(..))")
    public void before(){
        System.out.println("log start.");
    }
    
    @After(value="execution(* com.demon.java8.spring.aop.annotation.*.*(..))")
    public void after(){
        System.out.println("log end.");
    }
    
}
