package com.demon.concurrency.chapter1;

import java.util.Random;

/**
 * <pre>
 * 线程组异常处理
 * 建立一个方法来捕获线程组中的任何线程对象抛出的非捕获异常
 * 当线程抛出非捕获异常时，JVM 将为这个异常寻找三种可能的处理器：
 *   首先，寻找抛出异常的线程的非捕获异常处理器；
 *   如果不存在，JVM 继续查找这个线程所在的线程组的非捕获异常处理器；
 *   如果也不存在，JVM 将寻找默认的非捕获异常处理器；
 *   如果异常处理器都不存在，则打印异常堆栈信息，并退出线程
 * </pre>
 */
public class Test_1_12 {

	public static void main(String[] args) {
		MyThreadGroup group = new MyThreadGroup("MyThreadGroup");
		Task12 task = new Task12();
		for(int i=0;i<2;i++){
			Thread t = new Thread(group, task);
			t.start();
		}
	}
}

class MyThreadGroup extends ThreadGroup {

	public MyThreadGroup(String name) {
		super(name);
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.printf("The thread %s has throws and exception\n",Thread.currentThread().getId());
		e.printStackTrace();
		System.out.println("Terminating the rest of Threads");
		interrupt(); //调用父类的方法，中断所有线程
	}
	
}

class Task12 implements Runnable {

	@Override
	public void run() {
		int result;
		Random random = new Random(Thread.currentThread().getId());
		while(true){
			result = 1000/(int)(random.nextDouble()*1000);
			System.out.printf("%s : %d\n",Thread.currentThread().getId(),result);
			if(Thread.currentThread().isInterrupted()){
				System.out.printf("%d : Interrupted\n",Thread.currentThread().getId());
				return;
			}
		}
	}
	
}
