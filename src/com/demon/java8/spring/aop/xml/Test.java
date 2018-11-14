package com.demon.java8.spring.aop.xml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Test {

    public void test(){
        System.out.println("main test");
    }
    
    public void say() {
        System.out.println("Hello World");
    }
    
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("com/demon/java8/spring/aop/xml/spring.xml");
        Test test = (Test)ac.getBean("test");
        test.test();
        test.say();
    }
    
}
