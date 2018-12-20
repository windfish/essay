package com.demon.spring.custom.aop.aspect;

/**
 * 类过滤器
 * @author xuliang
 * @since 2018年12月20日 下午1:47:23
 *
 */
public interface ClassFilter {

    /**
     * 类的匹配结果
     * @param clazz 待匹配的Class
     */
    boolean matcher(Class<?> clazz) throws Exception;
    
}
