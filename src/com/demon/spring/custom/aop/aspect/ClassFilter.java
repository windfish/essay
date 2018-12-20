package com.demon.spring.custom.aop.aspect;

public interface ClassFilter {

    boolean matcher(Class<?> clazz) throws Exception;
    
}
