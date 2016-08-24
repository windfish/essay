package com.demon.concurrency.chapter8;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 监控执行器框架
 * @author fish
 * @version 2016年8月24日 下午3:21:33
 */
public class Test_8_4 {

	public static void main(String[] args) throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		Random random = new Random();
		for(int i=0;i<10;i++){
			Task4 task = new Task4(random.nextInt(10000));
			executor.submit(task);
		}
		for(int i=0;i<5;i++){
			showLog(executor);
			TimeUnit.SECONDS.sleep(1);
		}
		executor.shutdown();
		for(int i=0;i<5;i++){
			showLog(executor);
			TimeUnit.SECONDS.sleep(1);
		}
		executor.awaitTermination(1, TimeUnit.DAYS);
		System.out.println("Main: End.");
	}
	
	private static void showLog(ThreadPoolExecutor executor){
		System.out.println("************************");
		System.out.println("Executor Log");
		//线程池的核心线程数。当执行器不执行时，在内部线程池中的最小线程数
		System.out.println("Executor: Core Pool Size: "+executor.getPoolSize());
		//当前正在执行的线程数
		System.out.println("Executor: Active Count: "+executor.getActiveCount());
		//计划将被执行的任务数
		System.out.println("Executor: Task Count: "+executor.getTaskCount());
		//已被执行器执行且已完成执行的任务数
		System.out.println("Executor: Completed Task Count: "+executor.getCompletedTaskCount());
		//调用了shutdown()方法后，返回true
		System.out.println("Executor: Shutdown:"+executor.isShutdown());
		//执行器正在执行shutdown()方法但还没执行完成时，返回true
		System.out.println("Executor: Terminating: "+executor.isTerminating());
		//执行器完成执行时，返回true
		System.out.println("Executor: Terminated: "+executor.isTerminated());
		System.out.println("************************");
	}

}

class Task4 implements Runnable {
	private long milliseconds;

	public Task4(long milliseconds) {
		super();
		this.milliseconds = milliseconds;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+": Begin");
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+": End");
	}
	
}
