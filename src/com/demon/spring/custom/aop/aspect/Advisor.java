package com.demon.spring.custom.aop.aspect;

import org.aopalliance.aop.Advice;

/**
 * 通知接口
 * @author xuliang
 * @since 2018年12月20日 下午2:25:40
 *
 */
public interface Advisor {

    /**
     * 具体通知
     */
    Advice getAdvice();
    
}
