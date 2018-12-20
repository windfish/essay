package com.demon.spring.custom.aop.aspect;

import com.demon.spring.custom.aop.MethodMatcher;

public interface Pointcut {

    ClassFilter getClassFilter();
    
    MethodMatcher getMethodMatcher();
    
}
