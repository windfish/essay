package com.demon.spring.custom.simpleaop;

public class HelloServiceImpl implements HelloService {

    @Override
    public void sayHello() {
        System.out.println("Hello World");
    }

}
