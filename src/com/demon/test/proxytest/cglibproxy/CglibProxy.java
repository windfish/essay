package com.demon.test.proxytest.cglibproxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 *  cglib的动态代理：
 *   代理对象是目标对象的子类
 *   拦截器必须实现MethodInterceptor接口
 *   hibernate中session.load采用的是cglib实现的
 * 
 * @author xuliang
 * @since 2018年10月12日 下午3:43:19
 *
 */
public class CglibProxy implements MethodInterceptor {

    private Object obj;
    
    public CglibProxy(Class<?> clazz) {
        try {
            this.obj = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("before cglib proxy execute");
        
        Object invoke = method.invoke(obj, args);
        
        System.out.println("after cglib proxy execute");
        
        return invoke;
    }
    
    public static void main(String[] args) {
        CglibProxy proxy = new CglibProxy(TargetObject.class);
        
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(proxy); // 回调函数，拦截器
        enhancer.setSuperclass(TargetObject.class); // 设置代理对象的父类，cglib 生成的代理对象是目标对象的子类
        TargetObject obj = (TargetObject) enhancer.create();
        obj.execute();
    }

}
