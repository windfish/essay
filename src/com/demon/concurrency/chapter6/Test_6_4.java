package com.demon.concurrency.chapter6;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * 使用按优先级排序的阻塞式线程安全列表
 * @author fish
 * @version 2016年8月2日 下午4:41:48
 */
public class Test_6_4 {

	public static void main(String[] args) {
		PriorityBlockingQueue<Event> queue = new PriorityBlockingQueue<Event>();
		Thread taskThreads[] = new Thread[5];
		for(int i=0;i<taskThreads.length;i++){
			Task4 task = new Task4(i, queue);
			taskThreads[i] = new Thread(task);
			taskThreads[i].start();
		}
		for(int i=0;i<taskThreads.length;i++){
			try {
				taskThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Main: Queue Size: "+queue.size());
		for(int i=0;i<taskThreads.length*1000;i++){
			Event event = queue.poll();
			System.out.printf("Thread %s: Priority %d\n",event.getThread(),event.getPriority());
		}
		System.out.println("Main: Queue Size: "+queue.size());
		System.out.println("Main: End of the program.");
	}

}

class Event implements Comparable<Event> {
	private int thread;
	private int priority;

	public Event(int thread, int priority) {
		super();
		this.thread = thread;
		this.priority = priority;
	}

	@Override
	public int compareTo(Event e) {
		if(this.priority > e.getPriority()){
			return -1;
		}else if(this.priority < e.getPriority()){
			return 1;
		}else{
			return 0;
		}
	}

	public int getThread() {
		return thread;
	}

	public int getPriority() {
		return priority;
	}
	
}

class Task4 implements Runnable {
	private int id;
	private PriorityBlockingQueue<Event> queue;

	public Task4(int id, PriorityBlockingQueue<Event> queue) {
		super();
		this.id = id;
		this.queue = queue;
	}

	@Override
	public void run() {
		for(int i=0;i<1000;i++){
			Event event = new Event(id, i);
			queue.add(event);
		}
	}
	
}
