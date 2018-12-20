package com.demon.spring.custom.aop;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * 方法封装调用，可以对方法调用时，修改原有的行为
 * @author xuliang
 * @since 2018年12月19日 上午11:25:30
 *
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    /**
     * 被代理的对象
     */
    protected Object target;
    /**
     * 被代理的方法
     */
    protected Method method;
    /**
     * 被代理的方法参数
     */
    protected Object[] args;
    
    public ReflectiveMethodInvocation(Object target, Method method, Object[] args) {
        super();
        this.target = target;
        this.method = method;
        this.args = args;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object proceed() throws Throwable {
        return method.invoke(target, args);
    }

    @Override
    public Method getMethod() {
        return method;
    }

}
