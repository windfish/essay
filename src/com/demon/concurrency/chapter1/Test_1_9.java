package com.demon.concurrency.chapter1;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * <pre>
 * 线程中非受检异常的处理
 * 受检异常（非运行时异常）需在throws语句中指定或在方法体内捕获，非受检异常（运行时异常）不需要声明也不需要捕获
 * 由于run()方法不支持throws语句，因此需要捕获并处理受检异常；当抛出非受检异常时，默认行为是控制台输出堆栈信息并退出程序
 * </pre>
 */
public class Test_1_9 {

	public static void main(String[] args) {
		Task task = new Task();
		Thread thread = new Thread(task);
		thread.setUncaughtExceptionHandler(new ExceptionHandler()); //为线程指定一个异常处理器
		thread.start();
	}
}

class Task implements Runnable {

	@Override
	public void run() {
		Integer.parseInt("tttt");
	}
	
}

/**
 * 处理运行时异常的类
 */
class ExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println("An exception has bean caught.");
		System.out.println("Thread: "+t.getId());
		System.out.printf("Exception: %s: %s\n",e.getClass().getName(),e.getMessage());
		e.printStackTrace();
		System.out.println("Thread status: "+t.getState());
	}
	
}
