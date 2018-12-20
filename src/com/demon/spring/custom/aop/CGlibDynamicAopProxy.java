package com.demon.spring.custom.aop;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * cglib 方式生成代理对象，偷懒用spring 里的CGLib 了
 * @author xuliang
 * @since 2018年12月20日 上午10:36:28
 *
 */
public class CGlibDynamicAopProxy extends AbstractAopProxy implements MethodInterceptor {

    private Enhancer enhancer;
    
    public CGlibDynamicAopProxy(AdvisorSupport advisorSupport) {
        super(advisorSupport);
        enhancer = new Enhancer();
    }

    @Override
    public Object getProxy() {
        enhancer.setCallback(this);
        enhancer.setSuperclass(advisorSupport.getTargetSource().getTargetClass()); // 设置代理对象的父类，cglib 生成的代理对象是目标对象的子类
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        MethodMatcher methodMatcher = advisorSupport.getMethodMatcher();
        
        // 对象方法符合匹配规则，则执行advice 逻辑
        if(methodMatcher != null 
                && methodMatcher.matcher(method, advisorSupport.getTargetSource().getTargetClass())){
            // 获取Advice，MethodInterceptor 继承了Advice
            org.aopalliance.intercept.MethodInterceptor methodInterceptor = advisorSupport.getMethodInterceptor();
            // 将bean 的method 封装为MethodInvocation，并传给Advice 对象执行通知逻辑
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advisorSupport.getTargetSource().getTarget(), method, args));
        }else{
            // 不符合匹配规则的，直接执行原始方法
            return method.invoke(advisorSupport.getTargetSource().getTarget(), args);
        }
    }

}
