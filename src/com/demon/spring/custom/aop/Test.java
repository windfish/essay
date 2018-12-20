package com.demon.spring.custom.aop;

import java.lang.reflect.Method;

import com.demon.spring.custom.simpleaop.HelloService;
import com.demon.spring.custom.simpleaop.HelloServiceImpl;

/**
 * aop 测试
 * @author xuliang
 * @since 2018年12月20日 下午3:13:34
 *
 */
public class Test {

    @org.junit.Test
    public void test(){
        HelloService service = new HelloServiceImpl();
        service.sayHello();
        
        System.out.println("------------aop-----------------");
        
        AdvisorSupport support = new AdvisorSupport();
        support.setMethodInterceptor(new LogInterceptor());
        
        TargetSource targetSource = new TargetSource(HelloServiceImpl.class, HelloServiceImpl.class.getInterfaces(), service);
        support.setTargetSource(targetSource);
        support.setMethodMatcher((Method method, Class<?> clazz) -> true);
        
        HelloService aopService = (HelloService) new JdkDynamicAopProxy(support).getProxy();
        aopService.sayHello();
    }

}


