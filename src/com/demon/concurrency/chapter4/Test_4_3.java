package com.demon.concurrency.chapter4;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 创建固定大小的线程池
 * @author fish
 * @version 2016年7月19日 下午4:34:45
 */
public class Test_4_3 {

	public static void main(String[] args) {
		Server3 server = new Server3();
		for(int i=0;i<100;i++){
			Task3 task = new Task3("Task #"+i);
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
class Task3 implements Runnable {
	//记录任务的创建时间
	private Date initDate;
	//记录任务的名称
	private String name;

	public Task3(String name) {
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
class Server3 {
	private ThreadPoolExecutor executor;
	
	public Server3() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	}
	
	public void executeTask(Task3 task){
		System.out.println("Server: A new task has arrived.");
		executor.execute(task);
		System.out.println("Server: Pool Size: "+executor.getPoolSize()); //显示执行器中的线程数
		System.out.println("Server: Active Count: "+executor.getActiveCount()); //显示正在执行的任务数
		System.out.println("Server: Completed Count: "+executor.getCompletedTaskCount()); //显示完成的任务数量
		System.out.println("Server: Task Count: "+executor.getTaskCount()); //显示执行器中有多少任务
	}
	
	public void endServer(){
		executor.shutdown();
	}
}
