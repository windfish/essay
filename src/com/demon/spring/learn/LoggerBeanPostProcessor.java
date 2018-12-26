package com.demon.spring.learn;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 
 * @author xuliang
 * @since 2018年12月24日 上午11:40:13
 *
 */
public class LoggerBeanPostProcessor implements BeanPostProcessor, InitializingBean {

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
    
    /**
     * init-method 初始化bean 的额外操作
     */
    public void initMethod(){
        System.out.println(this.getClass().getName() + " init-method");
    }
    
    /**
     * 注解方式，初始化bean 的额外操作
     */
    @PostConstruct
    public void initAnnotation(){
        System.out.println(this.getClass().getName() + " init annotation");
    }
    
    /**
     * 实现InitializingBean 接口的方式，初始化bean 的额外操作
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(this.getClass().getName() + " InitializingBean");
    }

}
