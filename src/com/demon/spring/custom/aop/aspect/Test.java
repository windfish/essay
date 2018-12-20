package com.demon.spring.custom.aop.aspect;

import com.demon.spring.custom.ioc.XmlBeanFactory;
import com.demon.spring.custom.simpleaop.HelloService;
import com.demon.spring.custom.simpleaop.HelloServiceNoInterface;

/**
 * ioc + aop 测试
 * @author xuliang
 * @since 2018年12月20日 下午3:13:20
 *
 */
public class Test {

    @org.junit.Test
    public void test() throws Exception{
        String location = "D:\\work\\git\\essay\\src\\com\\demon\\spring\\custom\\ioc\\ioc.xml";
        XmlBeanFactory factory = new XmlBeanFactory(location);
        
        System.out.println("-------------jdk proxy-----------");
        HelloService service = (HelloService) factory.getBean("helloService");
        service.sayHello();
        
        System.out.println("-------------cglib proxy-----------");
        HelloServiceNoInterface noInterService = (HelloServiceNoInterface) factory.getBean("helloServiceNoInterface");
        noInterService.sayHello();
    }
    
}
