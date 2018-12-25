package com.demon.spring.learn;

import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.demon.spring.learn.lookup_method.NewsProvider;
import com.demon.spring.learn.lookup_method.NewsProviderByAware;

/**
 * 
 * @author xuliang
 * @since 2018年12月21日 上午11:37:59
 *
 */
public class Test {

    private ApplicationContext ac;
    private static final String location = "com/demon/spring/learn/spring.xml";
    
    @Before
    public void init(){
        ac = new ClassPathXmlApplicationContext(location);
    }
    
    @org.junit.Test
    public void testAlias(){
        System.out.println(ac.getBean("alias-hello"));
        System.out.println(ac.getBean("alias-alias-hello"));
    }
    
    @org.junit.Test
    public void testFactoryBean(){
        System.out.println(ac.getBean("helloFactory"));
        System.out.println(ac.getBean("&helloFactory"));
    }
    
    @org.junit.Test
    public void testFactory(){
        System.out.println(ac.getBean("staticCar"));
        System.out.println(ac.getBean("nonStaticCar"));
    }
    
    @org.junit.Test
    public void testLookupMethod(){
        System.out.println("------------no prototype----------------");
        NewsProvider provider = (NewsProvider) ac.getBean("newsProvider");
        System.out.println(provider);
        System.out.println(provider.getNews());
        System.out.println(provider.getNews());
        
        System.out.println("------------aware-----------------------");
        NewsProviderByAware providerByAware = (NewsProviderByAware) ac.getBean("newsProviderByAware");
        System.out.println(providerByAware);
        System.out.println(providerByAware.getNews());
        System.out.println(providerByAware.getNews());
        
        System.out.println("------------lookup-method---------------");
        NewsProvider providerByLookupMethod = (NewsProvider) ac.getBean("newsProviderByLookupMethod");
        System.out.println(providerByLookupMethod);
        System.out.println(providerByLookupMethod.getNews());
        System.out.println(providerByLookupMethod.getNews());
    }
    
    @org.junit.Test
    public void testBeanPostProcessor(){
        
    }
    
}
