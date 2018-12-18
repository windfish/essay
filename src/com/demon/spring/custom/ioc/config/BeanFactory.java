package com.demon.spring.custom.ioc.config;

/**
 * 
 * @author xuliang
 * @since 2018年12月18日 上午10:34:15
 *
 */
public interface BeanFactory {

    Object getBean(String id) throws Exception;
    
}
