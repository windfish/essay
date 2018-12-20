package com.demon.spring.custom.aop.aspect;

import com.demon.spring.custom.aop.LogInterceptor;

/**
 * aspect aop切面，定义了何处执行（setExpression），定义了何时执行以及要执行的逻辑（setAdvice）
 * @author xuliang
 * @since 2018年12月19日 下午5:39:04
 *
 */
public class LogAdvisor extends AspectJExpressionPointcutAdvisor {

    public LogAdvisor() {
//        setExpression("within(com.demon.spring.custom.simpleaop.HelloServiceImpl)");
        setExpression("execution(public * com.demon.spring.custom.simpleaop.HelloService*.*())");
        setAdvice(new LogInterceptor());
    }
    
}
