package com.demon.spring.custom.simpleaop;

import org.junit.Test;

public class AOPTest {

    @Test
    public void aopTest(){
        // 1. 创建一个 MethodInvocation 实现类
        MethodInvocation logTask = new MethodInvocation() {
            @Override
            public void invoke() {
                System.out.println("log task start");
            }
        };
        HelloService helloService = new HelloServiceImpl();
        // 2. 创建一个 Advice
        Advice beforeAdvice = new BeforeAdvice(helloService, logTask);
        // 3. 为目标对象生成代理
        HelloService proxy = (HelloService) SimpleAOP.getProxy(helloService, beforeAdvice);
        
        proxy.sayHello();
    }
    
}
