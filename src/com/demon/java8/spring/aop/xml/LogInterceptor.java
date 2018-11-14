package com.demon.java8.spring.aop.xml;

import org.springframework.stereotype.Component;

@Component
public class LogInterceptor {

    public void before(){
        System.out.println("log start.");
    }
    
    public void after(){
        System.out.println("log end.");
    }
    
}
