package com.demon.java8;

import org.junit.Test;

public class TestFunctionalClass implements TestFunctionalInterface {

	@Override
	public void test() {
		System.out.println("implements interface method");
	}
	
	@Test
	public void testByJUnit() {
		TestFunctionalClass t = new TestFunctionalClass();
		t.test();
		t.testDefaultMethod();
		TestFunctionalInterface.testStaticMethod();
	}

}
