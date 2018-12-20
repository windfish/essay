package com.demon.spring.custom.aop;

/**
 * 
 * @author xuliang
 * @since 2018年12月19日 上午11:04:40
 *
 */
public abstract class AbstractAopProxy implements AopProxy {

   protected AdvisorSupport advisorSupport;

    public AbstractAopProxy(AdvisorSupport advisorSupport) {
        super();
        this.advisorSupport = advisorSupport;
    }
    
}
