package com.demon.spring.custom.aop;

/**
 * aop 代理抽象类
 * @author xuliang
 * @since 2018年12月19日 上午11:04:40
 *
 */
public abstract class AbstractAopProxy implements AopProxy {

    /**
     * 生成代理对象的辅助对象，包含要代理的目标对象信息TargetSource、方法的拦截器MethodInterceptor、方法的匹配器MethodMatcher
     */
    protected AdvisorSupport advisorSupport;

    public AbstractAopProxy(AdvisorSupport advisorSupport) {
        super();
        this.advisorSupport = advisorSupport;
    }
    
}
