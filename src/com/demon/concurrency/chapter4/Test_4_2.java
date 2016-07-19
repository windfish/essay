package com.demon.concurrency.chapter4;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用线程执行器  TreadPoolExecutor
 * 
 * @author fish
 * @version 2016年7月19日 下午4:07:24
 */
public class Test_4_2 {

	public static void main(String[] args) {
		Server2 server = new Server2();
		for(int i=0;i<100;i++){
			Task2 task = new Task2("Task #"+i);
			server.executeTask(task);
		}
		server.endServer();
	}

}

/**
 * 模拟任务类
 * @author fish
 * @version 2016年7月19日 下午4:09:02
 */
class Task2 implements Runnable {
	//记录任务的创建时间
	private Date initDate;
	//记录任务的名称
	private String name;

	public Task2(String name) {
		super();
		this.initDate = new Date();
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+": Task "+name+": Created on: "+initDate);
		System.out.println(Thread.currentThread().getName()+": Task "+name+": Started on: "+new Date());
		try {
			long duration = (long) (Math.random()*10);
			System.out.println(Thread.currentThread().getName()+": Task "+name+": Doing during "+duration+" seconds.");
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+": Task "+name+": Finished on: "+new Date());
	}
	
}

/**
 * 模拟服务端
 * @author fish
 * @version 2016年7月19日 下午4:13:19
 */
class Server2 {
	private ThreadPoolExecutor executor;
	
	public Server2() {
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}
	
	public void executeTask(Task2 task){
		System.out.println("Server: A new task has arrived.");
		executor.execute(task);
		System.out.println("Server: Pool Size: "+executor.getPoolSize());
		System.out.println("Server: Active Count: "+executor.getActiveCount());
		System.out.println("Server: Completed Count: "+executor.getCompletedTaskCount());
	}
	
	public void endServer(){
		executor.shutdown();
	}
}
