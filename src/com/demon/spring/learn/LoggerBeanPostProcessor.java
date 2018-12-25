package com.demon.spring.learn;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 
 * @author xuliang
 * @since 2018年12月24日 上午11:40:13
 *
 */
public class LoggerBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("Befor "+beanName+" Initialization.");
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("After "+beanName+" Initialization.");
        return bean;
    }

}
