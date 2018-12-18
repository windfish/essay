package com.demon.spring.custom.ioc.config;

/**
 * 对外扩展接口，用途是在bean 初始化期间，可以对bean 进行一些处理，比如：AOP 就是在这里将切面逻辑织入bean 中的
 * @author xuliang
 * @since 2018年12月18日 上午11:46:24
 *
 */
public interface BeanPostProcessor {

    Object postProcessorBeforeInitialization(Object bean, String beanName) throws Exception;
    
    Object postProcessorAfterInitialization(Object bean, String beanName) throws Exception;
    
}
