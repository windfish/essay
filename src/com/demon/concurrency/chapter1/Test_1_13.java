package com.demon.concurrency.chapter1;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 使用工厂类创建线程：通过ThreadFactory 创建线程
 */
public class Test_1_13 {

	public static void main(String[] args) {
		MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
		Task13 task = new Task13();
		Thread thread;
		System.out.println("Starting the Threads.");
		for(int i=0;i<10;i++){
			thread = factory.newThread(task);
			thread.start();
		}
		System.out.println("Factory stats:");
		System.out.println(factory.getStats());
	}
}

class MyThreadFactory implements ThreadFactory {

	private int counter; //储存线程对象的数量
	private String name; //储存线程的名字
	private List<String> stats; //储存线程对象的统计数据
	
	public MyThreadFactory(String name) {
		this.counter = 0;
		this.name = name;
		this.stats = new ArrayList<String>();
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r,name+"-Thread_"+counter);
		counter++;
		stats.add(String.format("Create thread %d with name %s on %s\n", t.getId(),t.getName(),new Date()));
		return t;
	}
	
	public String getStats(){
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = stats.iterator();
		while(it.hasNext()){
			sb.append(it.next());
		}
		return sb.toString();
	}
	
}

class Task13 implements Runnable {

	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
