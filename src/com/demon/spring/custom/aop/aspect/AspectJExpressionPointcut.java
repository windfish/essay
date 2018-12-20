package com.demon.spring.custom.aop.aspect;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

import com.demon.spring.custom.aop.MethodMatcher;

/**
 * AspectJ Expression 方式匹配切入点
 * @author xuliang
 * @since 2018年12月19日 下午4:00:49
 *
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    private PointcutParser pointcutParser;
    private String expression;
    private PointcutExpression pointcutExpression;
    private static final Set<PointcutPrimitive> DEFAULT_PRIMITIVES = new HashSet<>();
    
    static{
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.ARGS);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.THIS);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.TARGET);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        DEFAULT_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }
    
    public AspectJExpressionPointcut() {
        this(DEFAULT_PRIMITIVES);
    }
    
    public AspectJExpressionPointcut(Set<PointcutPrimitive> primitives){
        pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(primitives);
    }

    /**
     * 使用 AspectJ Expression 匹配方法
     */
    @Override
    public boolean matcher(Method method, Class<?> clazz) {
        checkReadyToMatch();
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        if(shadowMatch.alwaysMatches()){
            return true;
        }
        return false;
    }

    /**
     * 使用 AspectJ Expression 匹配类
     */
    @Override
    public boolean matcher(Class<?> clazz) throws Exception {
        checkReadyToMatch();
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
    
    private void checkReadyToMatch(){
        if(this.expression == null){
            throw new IllegalStateException("must set property 'expression' before attempting to match");
        }
        if(pointcutExpression == null){
            pointcutExpression = pointcutParser.parsePointcutExpression(this.expression);
        }
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
    
}
