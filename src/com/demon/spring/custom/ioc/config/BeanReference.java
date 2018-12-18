package com.demon.spring.custom.ioc.config;

/**
 * bean 中ref 对应的值
 * @author xuliang
 * @since 2018年12月18日 上午10:07:52
 *
 */
public class BeanReference {

    private String name;
    private String ref;
    private Object bean;
    
    public BeanReference(String name, String ref) {
        this.name = name;
        this.ref = ref;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRef() {
        return ref;
    }
    public void setRef(String ref) {
        this.ref = ref;
    }
    public Object getBean() {
        return bean;
    }
    public void setBean(Object bean) {
        this.bean = bean;
    }
    
}
