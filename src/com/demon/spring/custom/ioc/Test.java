package com.demon.spring.custom.ioc;

import com.alibaba.fastjson.JSON;
import com.demon.spring.custom.simpleioc.Car;
import com.demon.spring.custom.simpleioc.Wheel;

/**
 * ioc 测试
 * @author xuliang
 * @since 2018年12月20日 下午3:13:55
 *
 */
public class Test {

    @org.junit.Test
    public void test() throws Exception{
        String location = "D:\\work\\git\\essay\\src\\com\\demon\\spring\\custom\\simpleioc\\ioc.xml";
        XmlBeanFactory factory = new XmlBeanFactory(location);
        Wheel wheel = (Wheel) factory.getBean("wheel");
        System.out.println(JSON.toJSONString(wheel));
        
        Car car = (Car) factory.getBean("car");
        System.out.println(JSON.toJSONString(car));
    }
    
}
