package com.demon.test;

public class Test {

	public static void main(String[] args) {
		//显示获取当前代码的行数
		System.out.println(new Throwable().getStackTrace()[0].getLineNumber());
	}

}
