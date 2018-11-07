package com.demon.java8.javaagent;

public class Test {

    public void print(){
        System.out.println("print test");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
