package com.demon.spring.custom.aop;

import java.lang.reflect.Method;

/**
 * 方法过滤器
 * @author xuliang
 * @since 2018年12月20日 下午1:48:39
 *
 */
public interface MethodMatcher {

    /**
     * 方法的匹配结果
     * @param method 待匹配的方法
     * @param clazz 待匹配的方法所属的Class
     */
    boolean matcher(Method method, Class<?> clazz);
    
}
