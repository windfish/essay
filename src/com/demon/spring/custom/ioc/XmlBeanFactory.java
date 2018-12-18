package com.demon.spring.custom.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demon.spring.custom.ioc.config.BeanDefinition;
import com.demon.spring.custom.ioc.config.BeanFactory;
import com.demon.spring.custom.ioc.config.BeanFactoryAware;
import com.demon.spring.custom.ioc.config.BeanPostProcessor;
import com.demon.spring.custom.ioc.config.BeanReference;
import com.demon.spring.custom.ioc.config.PropertyValue;

/**
 * bean factory
 * @author xuliang
 * @since 2018年12月18日 上午11:38:49
 *
 */
public class XmlBeanFactory implements BeanFactory {

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private List<String> beanDefinitionNames = new ArrayList<>();
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    private XmlBeanDefinitionReader beanDefinitionReader;
    
    public XmlBeanFactory(String location) throws Exception {
        beanDefinitionReader = new XmlBeanDefinitionReader();
        loadBeanDefinitions(location);
    }
    
    /**
     * 获取factory 的BeanDefinitionMap 中的bean，若bean 为null，则初始化bean 并返回（懒加载方式）
     */
    @Override
    public Object getBean(String id) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(id);
        if(beanDefinition == null){
            throw new IllegalArgumentException("no bean with name: "+id);
        }
        Object bean = beanDefinition.getBean();
        if(bean == null){
            bean = createBean(beanDefinition);
            bean = initializeBean(bean, id);
            beanDefinition.setBean(bean);
        }
        return bean;
    }
    
    /**
     * 1. 加载xml 配置
     * 2. 注册BeanDefinition 到factory 中
     * 3. 注册BeanPostProcessor，后续bean 初始化时，支持自定义的初始化方案 
     */
    private void loadBeanDefinitions(String location) throws Exception{
        beanDefinitionReader.loadBeanDefinition(location);
        registerBeanDefinition();
        registerBeanPostProcessor();
    }
    
    /**
     * 实例化bean，并初始化bean 的属性
     */
    private Object createBean(BeanDefinition beanDefinition) throws Exception{
        Object bean = beanDefinition.getClazz().newInstance();
        applyPropertyValues(bean, beanDefinition);
        return bean;
    }
    
    /**
     * 初始化bean 的属性，若属性关联其他bean，则先实例化其他bean
     */
    private void applyPropertyValues(Object bean, BeanDefinition beanDefinition) throws Exception{
        if(bean instanceof BeanFactoryAware){
            ((BeanFactoryAware)bean).setBeanFactory(this);
        }
        for(PropertyValue propertyValue: beanDefinition.getPropertyValues().getPropertyValues()){
            Object value = propertyValue.getValue();
            if(value instanceof BeanReference){
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getRef());
            }
            try{
                Method declaredMethod = bean.getClass().getDeclaredMethod("set" + propertyValue.getName().substring(0, 1).toUpperCase() 
                                                    + propertyValue.getName().substring(1), value.getClass());
                declaredMethod.setAccessible(true);
                
                declaredMethod.invoke(bean, value);
            }catch (NoSuchMethodException e) {
                Field declaredField = bean.getClass().getDeclaredField(propertyValue.getName());
                declaredField.setAccessible(true);
                declaredField.set(bean, value);
            }
        }
    }
    
    /**
     * 自定义实例化bean，继承BeanPostProcessor 接口的bean
     */
    private Object initializeBean(Object bean, String id) throws Exception{
        for(BeanPostProcessor beanPostProcessor: beanPostProcessors){
            bean = beanPostProcessor.postProcessorBeforeInitialization(bean, id);
        }
        for(BeanPostProcessor beanPostProcessor: beanPostProcessors){
            bean = beanPostProcessor.postProcessorAfterInitialization(bean, id);
        }
        return bean;
    }
    
    private void registerBeanDefinition(){
        for(Map.Entry<String, BeanDefinition> entry: beanDefinitionReader.getBeans().entrySet()){
            String id = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            beanDefinitionMap.put(id, beanDefinition);
            beanDefinitionNames.add(id);
        }
    }
    
    private void registerBeanPostProcessor() throws Exception{
        List<Object> beans = getBeansForType(BeanPostProcessor.class);
        for(Object bean: beans){
            beanPostProcessors.add((BeanPostProcessor)bean);
        }
    }
    
    /**
     * 根据传入的Class 匹配bean，若Class 为bean 的同一个类或接口，或是其超类或超接口，则返回
     */
    private List<Object> getBeansForType(Class<?> clazz) throws Exception{
        List<Object> beans = new ArrayList<>();
        for(String beanDefinitionName: beanDefinitionNames){
            if(clazz.isAssignableFrom(beanDefinitionMap.get(beanDefinitionName).getClazz())){
                beans.add(getBean(beanDefinitionName));
            }
        }
        return beans;
    }

}
