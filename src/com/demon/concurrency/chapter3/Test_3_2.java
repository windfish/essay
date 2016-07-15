package com.demon.concurrency.chapter3;

import java.util.concurrent.Semaphore;

/**
 * 资源的并发访问控制，使用信号量 Semaphore
 * 信号量是一种计数器，用来保护一个或多个共享资源的访问
 * 如果一个线程要访问一个资源，他必须先获得信号量
 *   若信号量内部的计数器大于0，则减1，线程获得访问资源的权限
 *   若信号量内部的计数器等于0，会将线程置入休眠状态直到
 *   当线程使用完资源后，释放信号量，内部计数器加1
 */
public class Test_3_2 {

	public static void main(String[] args) {
		PrintQueue queue = new PrintQueue();
		Thread threads[] = new Thread[10];
		for(int i=0;i<10;i++){
			threads[i] = new Thread(new Job(queue),"Thread #"+i);
		}
		for(int i=0;i<10;i++){
			threads[i].start();
		}
	}
}

class PrintQueue {
	private final Semaphore semaphore;
	
	public PrintQueue() {
		semaphore = new Semaphore(1);
	}
	
	public void printJob(Object document){
		try {
			semaphore.acquire(); //获得信号量
			long duration = (long) (Math.random()*10);
			System.out.println(Thread.currentThread().getName()+": PrintQueue: Printing Job during "+duration+" seconds");
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			semaphore.release(); //释放信号量
		}
	}
}

class Job implements Runnable {

	private PrintQueue queue;
	
	public Job(PrintQueue queue) {
		this.queue = queue;
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+": Going to print a job");
		queue.printJob(new Object());
		System.out.println(Thread.currentThread().getName()+": The document has bean done");
	}
	
}
