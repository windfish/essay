package com.demon.test.proxytest.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk的动态代理：
 *  代理对象和目标对象实现了共同的接口
 *  拦截器必须实现InvocationHanlder接口
 *  
 * @author xuliang
 * @since 2018年10月12日 下午3:43:00
 *
 */
public class DynamicProxy implements InvocationHandler {

    private Object target;
    
    public DynamicProxy(Class<?> clazz) {
        try {
            this.target = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before proxy execute");
        
        Object invoke = method.invoke(target, args);
        
        System.out.println("after proxy execute");
        
        return invoke;
    }
    
    public static void main(String[] args) {
        DynamicProxy proxy1 = new DynamicProxy(InterOneImpl.class);
        Inter i1 = (Inter)Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), new Class[]{Inter.class}, proxy1);
        i1.execute();
        
        DynamicProxy proxy2 = new DynamicProxy(InterTwoImpl.class);
        Inter i2 = (Inter)Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), new Class[]{Inter.class}, proxy2);
        i2.execute();
    }

}
