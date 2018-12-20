package com.demon.spring.custom.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * @author xuliang
 * @since 2018年12月19日 下午5:38:03
 *
 */
public class LogInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation paramMethodInvocation) throws Throwable {
        System.out.println(paramMethodInvocation.getMethod().getName() + " start");
        Object proceed = paramMethodInvocation.proceed();
        System.out.println(paramMethodInvocation.getMethod().getName() + " end");
        return proceed;
    }

}
