package com.demon.spring.learn;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;

/**
 * 初始化和销毁 bean 的回调
 * @author xuliang
 * @since 2018年12月27日 下午1:42:34
 *
 */
public class BeanCallback implements InitializingBean, DisposableBean {

    public void init1(){
        System.out.println("bean init-method");
    }
    
    @PostConstruct
    public void init2(){
        System.out.println("bean init @PostConstruct");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("bean init implements InitializingBean");
    }
    
    @Bean(initMethod="init3", destroyMethod="destroy3")
    public BeanCallback get(){
        return new BeanCallback();
    }
    public void init3(){
        System.out.println("bean init @Bean");
    }
    public void destroy3(){
        System.out.println("bean destroy @Bean");
    }
    
    public void destroy1(){
        System.out.println("bean destroy-method");
    }

    @PreDestroy
    public void destroy2(){
        System.out.println("bean destroy @PreDestroy");
    }
    
    @Override
    public void destroy() throws Exception {
        System.out.println("bean destroy implements DisposableBean");
    }
    
}
