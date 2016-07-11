package com.demon.concurrency.chapter1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 局部变量的使用
 * 如果创建的对象实现了Runnable接口，并作为参数创建多个线程对象，那么这些线程共享相同的属性，在一个线程中改变属性，所有线程都会受到影响
 * 当属性不需要被所有线程共享时，可使用线程局部变量（Thread-local Variable）
 * </pre>
 */
public class Test_1_10 {

	public static void main(String[] args) {
		//startDate 属性线程共享，同一个线程中其属性值在开始与结束时可能会不同
		//UnsafeTask task = new UnsafeTask();
		//startDate 属性线程不共享，同一个线程中其属性值在开始与结束时一定相同
		SafeTask task = new SafeTask();
		for(int i=0;i<10;i++){
			Thread thread = new Thread(task);
			thread.start();
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}

class UnsafeTask implements Runnable {

	private Date startDate;
	
	@Override
	public void run() {
		startDate = new Date();
		System.out.printf("Starting Thread: %s : %s\n",Thread.currentThread().getId(),startDate);
		try {
			TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("Finashed Thread: %s : %s\n",Thread.currentThread().getId(),startDate);
	}
	
}

class SafeTask implements Runnable {

	private static ThreadLocal<Date> startDate = new ThreadLocal<Date>(){
		protected Date initialValue() {
			return new Date();
		}
	};
	
	@Override
	public void run() {
		System.out.printf("Starting Thread: %s : %s\n",Thread.currentThread().getId(),startDate.get());
		try {
			TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("Finashed Thread: %s : %s\n",Thread.currentThread().getId(),startDate.get());
	}
	
}


