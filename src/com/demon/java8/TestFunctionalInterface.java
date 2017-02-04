package com.demon.java8;

import org.junit.Test;

/**
 * FunctionalInterface 注解修饰的接口为函数接口，只允许有一个接口方法（未实现的）
 * 但可以有默认方法（default修饰），也可以有静态方法（static修饰）
 * 
 * @author xuliang
 * @since 2017年2月4日 上午10:02:43
 *
 */
@FunctionalInterface
public interface TestFunctionalInterface {

	void test();
	
	default void testDefaultMethod(){
		System.out.println("interface default method");
	}
	
	static void testStaticMethod(){
		System.out.println("interface static method");
	}
	
}
