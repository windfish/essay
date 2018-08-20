package com.demon.jvm._2memory;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * 方法区内存溢出
 * 借助 CGLib 生成大量的类填满方法区
 * VM Args: -XX:PermSize=10m -XX:MaxPermSize=10m
 * @author xuliang
 * @since 2018年8月20日 下午3:59:23
 *
 */
public class JavaMethodAreaOOM {

    public static void main(String[] args) {
        while(true){
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    return proxy.invokeSuper(obj, args);
                }
            });
            enhancer.create();
        }
    }
    
    static class OOMObject {
        
    }
    
}
