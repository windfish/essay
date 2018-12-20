package com.demon.spring.custom.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * 通知辅助对象，包含目标对象信息、方法的拦截器、方法的匹配器MethodMatcher
 * @author xuliang
 * @since 2018年12月19日 上午10:59:24
 *
 */
public class AdvisorSupport {

    private TargetSource targetSource;
    private MethodInterceptor methodInterceptor;
    private MethodMatcher methodMatcher;
    
    public TargetSource getTargetSource() {
        return targetSource;
    }
    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }
    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }
    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }
    
}
