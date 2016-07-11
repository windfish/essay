package com.demon.concurrency.chapter1;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 守护线程的创建和运行
 * 守护线程优先级很低，当同一程序里没有其他线程运行时，守护线程才运行，守护线程执行结束后，JVM 也就结束了这个程序
 * 守护线程通常被用来作为同一程序中普通线程的服务提供者，它们通常是无限循环的，以等待服务请求或执行线程任务
 * 一个典型的守护线程是Java 的垃圾回收器
 * </pre>
 */
public class Test_1_8 {

	/*
	 * 10s后，也就是队列中有30个事件时，当三个线程都休眠时，守护线程才运行
	 */
	public static void main(String[] args) {
		Deque<Event> deque = new ArrayDeque<>();
		WriteTask write = new WriteTask(deque);
		for(int i=0;i<3;i++){
			Thread thread = new Thread(write);
			thread.start();
		}
		CleanerTask cleaner = new CleanerTask(deque);
		cleaner.start();
	}
}

class WriteTask implements Runnable {

	private Deque<Event> deque;
	
	public WriteTask(Deque<Event> deque) {
		this.deque = deque;
	}
	
	@Override
	public void run() {
		for(int i=1;i<100;i++){
			Event event = new Event();
			event.setDate(new Date());
			event.setEvent(String.format("The thread %s has generated an event",Thread.currentThread().getId()));
			deque.addFirst(event);
			System.out.printf(Thread.currentThread().getName()+": size of queue: %d\n",deque.size());
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}

class CleanerTask extends Thread {

	private Deque<Event> deque;
	
	public CleanerTask(Deque<Event> deque) {
		this.deque = deque;
		setDaemon(true); //设置为守护线程
	}
	
	@Override
	public void run() {
		while(true){
			Date date = new Date();
			clean(date);
		}
	}
	
	/**
	 * 读取队列最后一个事件对象，如果事件是10s前创建的，删除然后检查下一个；如果有事件删除，打印删除事件的信息和队列长度
	 */
	private void clean(Date date){
		long difference = 0;
		boolean delete = false;
		if(deque.size() == 0){
			return;
		}
		do {
			Event e = deque.getLast();
			difference = date.getTime() - e.getDate().getTime();
			if(difference > 10000){
				System.out.printf("Cleaner: %s\n",e.getEvent());
				deque.removeLast();
				delete = true;
			}
		}while(difference > 10000);
		if(delete){
			System.out.printf("Cleaner: size of the queue: %d\n",deque.size());
		}
	}
	
}

class Event {
	private Date date;
	private String event;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
}
