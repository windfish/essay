package com.demon.spring.custom.ioc.config;

/**
 * 
 * @author xuliang
 * @since 2018年12月18日 上午10:36:41
 *
 */
public interface BeanFactoryAware {

    void setBeanFactory(BeanFactory beanFactory) throws Exception;
    
}
