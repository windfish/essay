package com.demon.spring.custom.aop.aspect;

/**
 * 切面接口
 * @author xuliang
 * @since 2018年12月20日 下午2:26:12
 *
 */
public interface PointcutAdvisor extends Advisor {

    /**
     * 切面的切入点
     */
    Pointcut getPointcut();
    
}
