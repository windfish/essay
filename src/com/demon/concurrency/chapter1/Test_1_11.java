package com.demon.concurrency.chapter1;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 线程分组：把一个组的线程当成一个单一的单元，对组内的线程对象进行访问并操作
 * ThreadGroup 表示一组线程，线程组可以包含线程对象，也可以包含其他的线程组对象，它是一个树形结构
 * </pre>
 */
public class Test_1_11 {

	/*
	 * 创建10个线程，然后休眠一个随机时间（模拟一个查询），当一个线程查询成功时，中断其他9个线程
	 */
	public static void main(String[] args) {
		ThreadGroup group = new ThreadGroup("Searcher");
		Result result = new Result();
		SearchTask searchTask = new SearchTask(result);
		for(int i=0;i<10;i++){
			Thread thread = new Thread(group, searchTask);
			thread.start();
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//打印线程组信息
		System.out.printf("Number of Threads: %d\n",group.activeCount());
		System.out.println("ThreadGroup list:");
		group.list();
		//获取线程组里的线程信息
		Thread[] threads = new Thread[group.activeCount()];
		group.enumerate(threads);
		for(int i=0;i<group.activeCount();i++){
			System.out.printf("Thread %s: %s\n",threads[i].getName(),threads[i].getState());
		}
		waitFinish(group);
		group.interrupt();
	}
	
	private static void waitFinish(ThreadGroup group){
		while(group.activeCount() > 9){
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * 存储先执行完的线程
 */
class Result {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

class SearchTask implements Runnable {

	private Result result;
	
	public SearchTask(Result result) {
		this.result = result;
	}
	
	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		System.out.printf("Thread %s: Start\n",name);
		try {
			doTask();
			result.setName(name);
		} catch (InterruptedException e) {
			System.out.printf("Thread %s: Interrupted\n",name);
			return;
		}
		System.out.printf("Thread %s: End\n",name);
	}
	
	private void doTask() throws InterruptedException {
		Random random = new Random(new Date().getTime());
		int value = (int) (random.nextDouble()*100);
		System.out.printf("Thread %s: %d\n",Thread.currentThread().getName(),value);
		TimeUnit.SECONDS.sleep(value);
	}
	
}
