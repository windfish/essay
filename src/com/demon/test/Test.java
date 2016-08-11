package com.demon.test;

public class Test {

	public static void main(String[] args) {
		System.out.println("税后new: "+(14500*13+450*12+600*12+600));
		System.out.println("税后old: "+(10500*13+3360*12+2100));
		
		System.out.println("税前new: "+(16000*13+450*12+300*12));
		System.out.println("税前old: "+(14000*13+450*12+1680*12));
		
		//显示获取当前代码的行数
		System.out.println(new Throwable().getStackTrace()[0].getLineNumber());
		
		String s = "aaa";
		test(s);
		System.out.println(s);
		
		int i=0;
		testInt(i);
		System.out.println(i);
		
		StringBuffer sb = new StringBuffer();
		testStringBuffer(sb);
		System.out.println(sb);
	}
	
	public static void test(String str){
		str = "123";
	}
	
	public static void testInt(int a){
		a=3;
	}
	
	public static void testStringBuffer(StringBuffer sb){
		sb.append("123123");
	}

}
