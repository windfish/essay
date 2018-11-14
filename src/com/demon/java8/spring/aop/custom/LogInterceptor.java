package com.demon.java8.spring.aop.custom;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogInterceptor {

    /*
     * 可以为多个advice 使用一个注解
     * 也可以使用不同的注解
     */
    
    @Before(value="@annotation(com.demon.java8.spring.aop.custom.Log)")
    public void before(){
        System.out.println("log start.");
    }
    
    @After(value="@annotation(com.demon.java8.spring.aop.custom.Log)")
    public void after(){
        System.out.println("log end.");
    }
    
    @Before(value="@annotation(com.demon.java8.spring.aop.custom.LogBefore)")
    public void begin(){
        System.out.println("log start.");
    }
    
    @After(value="@annotation(com.demon.java8.spring.aop.custom.LogAfter)")
    public void end(){
        System.out.println("log end.");
    }
    
}
