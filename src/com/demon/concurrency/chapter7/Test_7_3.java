package com.demon.concurrency.chapter7;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 实现基于优先级的 Executor 类
 * @author fish
 * @version 2016年8月16日 下午3:02:30
 */
public class Test_7_3 {

	public static void main(String[] args) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1000, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
		for(int i=0;i<4;i++){
			MyPriorityTask task = new MyPriorityTask(i, "Task "+i);
			executor.execute(task);
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=4;i<8;i++){
			MyPriorityTask task = new MyPriorityTask(i, "Task "+i);
			executor.execute(task);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main: End.");
	}

}

class MyPriorityTask implements Runnable, Comparable<MyPriorityTask> {
	private int priority;
	private String name;

	public MyPriorityTask(int priority, String name) {
		super();
		this.priority = priority;
		this.name = name;
	}
	
	public int getPriority(){
		return this.priority;
	}

	@Override
	public int compareTo(MyPriorityTask o) {
		if(this.getPriority() < o.getPriority()){
			return 1;
		}
		if(this.getPriority() > o.getPriority()){
			return -1;
		}
		return 0;
	}

	@Override
	public void run() {
		System.out.printf("MyPriorityTask: %s Priority: %d\n",name,priority);
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
