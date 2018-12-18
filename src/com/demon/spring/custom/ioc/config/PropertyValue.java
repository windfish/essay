package com.demon.spring.custom.ioc.config;

/**
 * bean 的属性值
 * @author xuliang
 * @since 2018年12月18日 上午10:12:48
 *
 */
public class PropertyValue {

    private String name;
    private Object value;
    
    public PropertyValue() {
    }
    
    public PropertyValue(String name, Object value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
}
