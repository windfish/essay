package com.demon.java8.spring.aop.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.demon.java8.spring.aop.custom.Log;

@Component
public class Test {

    @Log
    public void test(){
        System.out.println("main test");
    }
    
    public void say() {
        System.out.println("Hello World");
    }
    
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("com/demon/java8/spring/aop/annotation/spring.xml");
        Test test = (Test)ac.getBean("test");
        test.test();
        test.say();
    }
    
}
