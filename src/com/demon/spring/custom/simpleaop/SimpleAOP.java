package com.demon.spring.custom.simpleaop;

import java.lang.reflect.Proxy;

/**
 * 
 * @author xuliang
 * @since 2018年12月17日 上午11:20:53
 *
 */
public class SimpleAOP {

    public static Object getProxy(Object bean, Advice advice){
        return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), advice);
    }
    
}
