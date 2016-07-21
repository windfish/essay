package com.demon.concurrency.chapter4;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 周期性的执行任务
 * @author fish
 * @version 2016年7月21日 下午3:39:32
 */
public class Test_4_8 {

	public static void main(String[] args) {
		ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
		System.out.println("Main: Starting at:"+new Date());
		Task8 task = new Task8("Task");
		
		/*
		 * 参数：
		 * 周期性执行的任务
		 * 第一次执行任务后的延时时间
		 * 两次执行的时间周期
		 * 两个时间的单位
		 * 
		 * 返回值：
		 * 泛型化接口，runnable没有泛型，使用?泛型化
		 */
		ScheduledFuture<?> result = executor.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);
		
		for(int i=0;i<10;i++){
			//返回任务到下一次执行时所需要的等待的剩余时间
			System.out.println("Main: Delay: "+result.getDelay(TimeUnit.MILLISECONDS));
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main: Finished at:"+new Date());
	}

}

class Task8 implements Runnable {
	private String name;

	public Task8(String name) {
		super();
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println(name+": Starting at:"+new Date());
	}
	
}
