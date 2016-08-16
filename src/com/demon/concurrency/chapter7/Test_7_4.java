package com.demon.concurrency.chapter7;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 实现 ThreadFactory 接口生成定制线程
 * @author fish
 * @version 2016年8月16日 下午4:18:28
 */
public class Test_7_4 {

	public static void main(String[] args) {
		MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
		MyTask task = new MyTask();
		Thread thread = factory.newThread(task);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main: Thread information.");
		System.out.println(thread);
		System.out.println("Main: End.");
	}

}

class MyThread extends Thread {
	private Date creationDate;
	private Date startDate;
	private Date finishDate;
	
	public MyThread(Runnable target, String name) {
		super(target, name);
		setCreationDate();
	}
	
	@Override
	public void run() {
		setStartDate();
		super.run();
		setFinishDate();
	}
	
	public void setCreationDate(){
		this.creationDate = new Date();
	}
	
	public void setStartDate(){
		this.startDate = new Date();
	}
	
	public void setFinishDate(){
		this.finishDate = new Date();
	}
	
	/**
	 * 计算线程开始和结束的时间差
	 */
	public long getExecutionTime(){
		return finishDate.getTime() - startDate.getTime();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(": ");
		sb.append(" CreateDate:");
		sb.append(creationDate);
		sb.append(" : Running time:");
		sb.append(getExecutionTime());
		sb.append(" MILLISECONDS.");
		return sb.toString();
	}
	
}

class MyThreadFactory implements ThreadFactory {
	private int counter;
	private String prefix;

	public MyThreadFactory(String prefix) {
		super();
		this.prefix = prefix;
		this.counter = 1;
	}

	@Override
	public Thread newThread(Runnable r) {
		MyThread myThread = new MyThread(r, prefix+"-"+counter);
		counter++;
		return myThread;
	}
	
}

class MyTask implements Runnable {

	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
