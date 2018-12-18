package com.demon.spring.custom.ioc.config;

/**
 * 加载BeanDefinition
 * @author xuliang
 * @since 2018年12月18日 上午10:32:28
 *
 */
public interface BeanDefinitionReader {

    void loadBeanDefinition(String location) throws Exception;
    
}
