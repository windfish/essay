package com.demon.spring.custom.aop;

/**
 * 代理的目标对象信息
 * @author xuliang
 * @since 2018年12月19日 上午10:51:05
 *
 */
public class TargetSource {

    private Class<?> targetClass;
    private Class<?>[] interfaces;
    private Object target;
    
    public TargetSource(Class<?> targetClass, Class<?>[] interfaces, Object target) {
        super();
        this.targetClass = targetClass;
        this.interfaces = interfaces;
        this.target = target;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
    public Class<?>[] getInterfaces() {
        return interfaces;
    }
    public Object getTarget() {
        return target;
    }
    
}
