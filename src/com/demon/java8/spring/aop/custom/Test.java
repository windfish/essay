package com.demon.java8.spring.aop.custom;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Test {

    @LogBefore
    @LogAfter
    public void test(){
        System.out.println("main test");
    }
    
    @Log
    public void say() {
        System.out.println("Hello World");
    }
    
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("com/demon/java8/spring/aop/custom/spring.xml");
        Test test = (Test)ac.getBean("test");
        test.test();
        System.out.println("------------------------------");
        test.say();
    }
    
}
