package com.demon.java8.javaagent;

public class JavaAgent {

    public void hello(){
        System.out.println("hello javaagent");
    }
    
    public void test(){
        System.out.println("no javaagent out");
    }
    
    public static void main(String[] args) {
        JavaAgent a = new JavaAgent();
        a.test();
        System.out.println("--------------------");
        a.hello();
    }
    
}
