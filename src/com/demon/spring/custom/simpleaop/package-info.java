/**
 * Spring AOP 的一些概念：
 * > 通知（Advice）
 *    通知定义了要织入目标对象的逻辑，以及执行时机
 *    Spring 中定义了五种不同类型的通知：
 *      前置通知（Before）：在目标方法执行前，执行通知
 *      后置通知（After）：在目标方法执行后，执行通知。此时不关心目标方法返回的结果是什么
 *      返回通知（After-returning）：在目标方法执行后，执行通知
 *      异常通知（After-throwing）：在目标方法抛出异常后，执行通知
 *      环绕通知（Around）：目标方法被通知包裹，通知在方法执行前和执行后都会被调用
 * > 切点（PonitCut）
 *    如果说通知定义了在何时执行通知，那么切点就定义了在何处执行通知。
 *    切点的作用就是，通过匹配规则查找合适的连接点（JoinPonit），AOP 会在这些连接点上织入通知
 * > 切面（Aspect）
 *    切面包含了通知和切点，通知和切点共同定义了切面是什么，在何时、何处执行切面逻辑
 */
package com.demon.spring.custom.simpleaop;