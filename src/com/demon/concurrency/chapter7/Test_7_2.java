package com.demon.concurrency.chapter7;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定制 ThreadPoolExecutor 类
 * @author fish
 * @version 2016年8月16日 上午11:24:05
 */
public class Test_7_2 {

	public static void main(String[] args) {
		MyExecutor executor = new MyExecutor(2, 2, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
		List<Future<String>> list = new ArrayList<Future<String>>();
		for(int i=0;i<10;i++){
			SleepTwoSecondsTask task = new SleepTwoSecondsTask();
			Future<String> future = executor.submit(task);
			list.add(future);
		}
		for(int i=0;i<5;i++){
			try {
				String result = list.get(i).get();
				System.out.println("Main: Result for Task "+i+": "+result);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		for(int i=5;i<10;i++){
			try {
				String result = list.get(i).get();
				System.out.println("Main: Result for Task "+i+": "+result);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main: End of program.");
	}

}

class MyExecutor extends ThreadPoolExecutor {
	private ConcurrentHashMap<String, Date> startTimes;

	public MyExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		startTimes = new ConcurrentHashMap<String, Date>();
	}
	
	/**
	 * 将已执行过的任务、正在执行的任务和等待执行的任务的信息输出到控制台
	 */
	@Override
	public void shutdown() {
		System.out.println("MyExecutor: Going to shutdown.");
		System.out.println("MyExecutor: Executed tasks: "+getCompletedTaskCount());
		System.out.println("MyExecutor: Running tasks: "+getActiveCount());
		System.out.println("MyExecutor: Pending tasks: "+getQueue().size());
		super.shutdown();
	}
	
	/**
	 * 将已执行过的任务、正在执行的任务和等待执行的任务的信息输出到控制台
	 */
	@Override
	public List<Runnable> shutdownNow() {
		System.out.println("MyExecutor: Going to immediately shutdown.");
		System.out.println("MyExecutor: Executed tasks: "+getCompletedTaskCount());
		System.out.println("MyExecutor: Running tasks: "+getActiveCount());
		System.out.println("MyExecutor: Pending tasks: "+getQueue().size());
		return super.shutdownNow();
	}
	
	/**
	 * 输出要执行的线程的名字、任务的哈希码，将开始日期存入HashMap中
	 */
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		System.out.println("MyExecutor: A task is beginning: "+t.getName()+": "+r.hashCode());
		startTimes.put(String.valueOf(r.hashCode()), new Date());
	}
	
	/**
	 * 将任务执行结果输出到控制台，并计算运行时间放入HashMap
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		Future<?> result = (Future<?>) r;
		try {
			System.out.println("*****************************");
			System.out.println("MyExecutor: A task is finishing.");
			System.out.println("MyExecutor: Result: "+result.get());
			Date startDate = startTimes.remove(String.valueOf(r.hashCode()));
			Date now = new Date();
			long diff = now.getTime() - startDate.getTime();
			System.out.println("MyExecutor: Duration: "+diff);
			System.out.println("*****************************");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}

class SleepTwoSecondsTask implements Callable<String> {

	@Override
	public String call() throws Exception {
		TimeUnit.SECONDS.sleep(2);
		return new Date().toString();
	}
	
}
