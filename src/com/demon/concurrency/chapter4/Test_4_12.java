package com.demon.concurrency.chapter4;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理在执行器中被拒绝的任务
 * shutdown() 之后，执行器会等待正在执行的任务完成，才会关闭执行器；
 * 若此时发送一个任务给执行器，则该任务会被拒绝。
 * 实现了 RejectedExecutionHandler 接口的任务，被拒绝后会有一套机制来处理
 * @author fish
 * @version 2016年7月29日 上午9:54:43
 */
public class Test_4_12 {

	public static void main(String[] args) {
		RejectedTaskController controller = new RejectedTaskController();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		executor.setRejectedExecutionHandler(controller);
		System.out.println("Main: Starting.");
		for(int i=0;i<3;i++){
			Task12 task = new Task12("Task "+i);
			executor.submit(task);
		}
		System.out.println("Main: Shutting down the executor.");
		executor.shutdown();
		
		System.out.println("Main: Send another task.");
		Task12 task = new Task12("Rejected task");
		executor.submit(task);
		System.out.println("Main: End.");
	}

}

class RejectedTaskController implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		System.out.println("RejectedTaskController: The task "+r.toString()+" has bean rejected.");
		System.out.println("RejectedTaskController: "+executor.toString());
		System.out.println("RejectedTaskController: Terminating: "+executor.isTerminating());
		System.out.println("RejectedTaskController: Terminated: "+executor.isTerminated());
	}
	
}

class Task12 implements Runnable {
	private String name;

	public Task12(String name) {
		super();
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println("Task "+name+": Starting.");
		try {
			long duration = (long) (Math.random()*10);
			System.out.println("Task "+name+": ReportGenerator: Generating a report during "+duration+" seconds.");
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Task "+name+": End.");
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
