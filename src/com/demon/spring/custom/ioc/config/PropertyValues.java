package com.demon.spring.custom.ioc.config;

import java.util.LinkedList;
import java.util.List;

/**
 * bean 的属性值集合
 * @author xuliang
 * @since 2018年12月18日 上午10:18:47
 *
 */
public class PropertyValues {

    private List<PropertyValue> propertyValues = new LinkedList<>();
    
    public void addPropertyValue(PropertyValue pv){
        propertyValues.add(pv);
    }
    
    public List<PropertyValue> getPropertyValues(){
        return this.propertyValues;
    }
    
}
