package com.demon.spring.custom.ioc.config;

/**
 * bean 的定义，根据Bean 的配置生成的详细对象
 * @author xuliang
 * @since 2018年12月18日 上午10:10:14
 *
 */
public class BeanDefinition {

    private String id;
    private Object bean;
    private String className;
    private Class<?> clazz;
    private PropertyValues propertyValues = new PropertyValues();
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Object getBean() {
        return bean;
    }
    public void setBean(Object bean) {
        this.bean = bean;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public Class<?> getClazz() {
        return clazz;
    }
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    public PropertyValues getPropertyValues() {
        return propertyValues;
    }
    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }
    
}
