package com.demon.concurrency.chapter7;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 在 Executor 中使用 ThreadFactory
 * @author fish
 * @version 2016年8月16日 下午5:08:49
 */
public class Test_7_5 {

	public static void main(String[] args) {
		MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
		ExecutorService executor = Executors.newCachedThreadPool(factory); //使用自定义的线程工厂
		MyTask task = new MyTask();
		executor.submit(task);
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main: End.");
	}

}
