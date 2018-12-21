package com.demon.spring.learn;

import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * @author xuliang
 * @since 2018年12月21日 下午2:39:38
 *
 */
public class HelloFactoryBean implements FactoryBean<HelloAlias> {

    @Override
    public HelloAlias getObject() throws Exception {
        HelloAlias hello = new HelloAlias();
        hello.setContent("hello by factory bean");
        return hello;
    }

    @Override
    public Class<?> getObjectType() {
        return HelloAlias.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
