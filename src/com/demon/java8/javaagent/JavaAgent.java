package com.demon.java8.javaagent;

public class JavaAgent {

    public void hello(){
        System.out.println("hello javaagent");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void test(){
        System.out.println("no javaagent out");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        JavaAgent a = new JavaAgent();
        a.test();
        System.out.println("--------------------");
        a.hello();
        System.out.println("--------------------");
        new Test().print();
    }
    
}
