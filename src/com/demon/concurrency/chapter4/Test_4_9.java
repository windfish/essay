package com.demon.concurrency.chapter4;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 执行器中取消任务
 * 使用Future 的cancel()方法
 * @author fish
 * @version 2016年7月21日 下午4:46:51
 */
public class Test_4_9 {

	public static void main(String[] args) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		Task9 task = new Task9();
		System.out.println("Main: Executing the task.");
		Future<String> result = executor.submit(task);
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Main: Cancel the task.");
		result.cancel(true);
		System.out.println("Main: Cancelled: "+result.isCancelled());
		System.out.println("Main: Done: "+result.isDone());
		
		executor.shutdown();
		System.out.println("Main: The exexutor has finished.");
	}

}

class Task9 implements Callable<String> {
	

	@Override
	public String call() throws Exception {
		while(true){
			System.out.println("Task: Test");
			Thread.sleep(100);
		}
	}
	
}
