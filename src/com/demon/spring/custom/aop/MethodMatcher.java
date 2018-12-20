package com.demon.spring.custom.aop;

import java.lang.reflect.Method;

public interface MethodMatcher {

    boolean matcher(Method method, Class<?> clazz);
    
}
