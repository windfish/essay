package com.demon.spring.custom.aop.aspect;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;

import com.demon.spring.custom.aop.TargetSource;
import com.demon.spring.custom.ioc.XmlBeanFactory;
import com.demon.spring.custom.ioc.config.BeanFactory;
import com.demon.spring.custom.ioc.config.BeanFactoryAware;
import com.demon.spring.custom.ioc.config.BeanPostProcessor;

/**
 * <pre>
 * aop 逻辑织入对象
 * 实现了 BeanFactoryAware，会在BeanFactory 初始化时，感知到BeanFactory，用于后续bean 对象的处理
 * 实现了 BeanPostProcessor，会在bean 初始化时，自定义一下实现，这里用以生成bean 的aop 代理类
 * </pre>
 * @author xuliang
 * @since 2018年12月19日 下午5:09:06
 *
 */
public class AspectJAwareAdvisorAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    private XmlBeanFactory xmlBeanFactory;
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws Exception {
        this.xmlBeanFactory = (XmlBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessorBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(Object bean, String beanName) throws Exception {
        if(bean instanceof AspectJExpressionPointcutAdvisor){
            return bean;
        }
        if(bean instanceof MethodInterceptor){
            return bean;
        }
        
        // 1. 从 BeanFactory 中查找 AspectJExpressionPointcutAdvisor 类型的对象
        List<Object> advisors = xmlBeanFactory.getBeansForType(AspectJExpressionPointcutAdvisor.class);
        for(Object obj: advisors){
            if(!(obj instanceof AspectJExpressionPointcutAdvisor)){
                continue;
            }
            AspectJExpressionPointcutAdvisor advisor = (AspectJExpressionPointcutAdvisor) obj;
            
            // 2. 使用Pointcut 匹配当前对象，匹配成功则创建代理对象
            if(advisor.getPointcut().getClassFilter().matcher(bean.getClass())){
                ProxyFactory proxyFactory = new ProxyFactory();
                proxyFactory.setMethodInterceptor((MethodInterceptor)advisor.getAdvice());
                proxyFactory.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
                proxyFactory.setTargetSource(new TargetSource(bean.getClass(), bean.getClass().getInterfaces(), bean));
                // 3. 生成bean 的代理对象
                return proxyFactory.getProxy();
            }
        }
        
        return bean;
    }

}
