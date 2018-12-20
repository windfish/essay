package com.demon.spring.custom.aop.aspect;

import com.demon.spring.custom.aop.MethodMatcher;

/**
 * 切入点接口
 * @author xuliang
 * @since 2018年12月20日 下午2:21:45
 *
 */
public interface Pointcut {

    /**
     * 类过滤器
     */
    ClassFilter getClassFilter();

    /**
     * 方法过滤器
     */
    MethodMatcher getMethodMatcher();
    
}
