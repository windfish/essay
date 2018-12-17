package com.demon.spring.custom.simpleaop;

import java.lang.reflect.Method;

/**
 * 前置通知
 * @author xuliang
 * @since 2018年12月17日 上午11:05:32
 *
 */
public class BeforeAdvice implements Advice {

    private Object bean;
    private MethodInvocation methodInvocation;
    
    public BeforeAdvice(Object bean, MethodInvocation methodInvocation) {
        this.bean = bean;
        this.methodInvocation = methodInvocation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        methodInvocation.invoke();
        return method.invoke(bean, args);
    }

}
