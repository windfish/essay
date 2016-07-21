package com.demon.concurrency.chapter4;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 延时执行任务  ScheduledThreadPoolExecutor
 * @author fish
 * @version 2016年7月21日 下午3:19:55
 */
public class Test_4_7 {

	public static void main(String[] args) {
		ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
		System.out.println("Main: Starting at:"+new Date());
		
		for(int i=0;i<5;i++){
			Task7 task = new Task7("Task "+i);
			/*
			 * 参数：
			 * 待执行的任务
			 * 任务执行前需等待的时间
			 * 等待的时间的单位
			 */
			executor.schedule(task, i+1, TimeUnit.SECONDS); 
		}
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main: End at:"+new Date());
	}

}

class Task7 implements Callable<String> {
	private String name;

	public Task7(String name) {
		super();
		this.name = name;
	}

	@Override
	public String call() throws Exception {
		System.out.println(name+": Starting at : "+new Date());
		return "Hello World";
	}
	
}
