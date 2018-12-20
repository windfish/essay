package com.demon.spring.custom.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * jdk 方式动态生成代理对象
 * @author xuliang
 * @since 2018年12月19日 上午11:16:17
 *
 */
public class JdkDynamicAopProxy extends AbstractAopProxy implements InvocationHandler {

    public JdkDynamicAopProxy(AdvisorSupport advisorSupport) {
        super(advisorSupport);
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(getClass().getClassLoader(), advisorSupport.getTargetSource().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodMatcher methodMatcher = advisorSupport.getMethodMatcher();
        
        // 对象方法符合匹配规则，则执行advice 逻辑
        if(methodMatcher != null 
                && methodMatcher.matcher(method, advisorSupport.getTargetSource().getTargetClass())){
            // 获取Advice，MethodInterceptor 继承了Advice
            MethodInterceptor methodInterceptor = advisorSupport.getMethodInterceptor();
            // 将bean 的method 封装为MethodInvocation，并传给Advice 对象执行通知逻辑
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advisorSupport.getTargetSource().getTarget(), method, args));
        }else{
            // 不符合匹配规则的，直接执行原始方法
            return method.invoke(advisorSupport.getTargetSource().getTarget(), args);
        }
    }

}
