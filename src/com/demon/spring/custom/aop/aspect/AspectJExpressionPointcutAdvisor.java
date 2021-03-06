package com.demon.spring.custom.aop.aspect;

import org.aopalliance.aop.Advice;

/**
 * 切面，包含AspectJ Expression 的切入点以及具体的通知
 * @author xuliang
 * @since 2018年12月19日 下午4:42:44
 *
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    private AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    private Advice advice;
    
    public void setExpression(String expression){
        pointcut.setExpression(expression);
    }
    
    public void setAdvice(Advice advice){
        this.advice = advice;
    }
    
    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

}
