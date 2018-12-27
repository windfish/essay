package com.demon.spring.learn;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <pre>
 * 重复id 和name 的bean，强制在启动时抛出异常
 * 针对web 环境，在web.xml 中添加配置：
 *  <context-param>
        <param-name>contextInitializer</param-name>
        <param-value>com.demon.spring.learn.XmlApplicationContextInitializer</param-value>
    </context-param>
 * 对于spring mvc，添加如下配置：
 *  <init-param>
        <param-name>contextInitializer</param-name>
        <param-value>com.demon.spring.learn.XmlApplicationContextInitializer</param-value>
    </init-param>
 * </pre>
 * 
 * @author xuliang
 * @since 2018年12月27日 上午10:58:30
 *
 */
public class XmlApplicationContextInitializer implements ApplicationContextInitializer<ClassPathXmlApplicationContext> {

    @Override
    public void initialize(ClassPathXmlApplicationContext applicationContext) {
        applicationContext.setAllowBeanDefinitionOverriding(false);
    }

}
