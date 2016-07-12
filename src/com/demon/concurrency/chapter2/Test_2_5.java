package com.demon.concurrency.chapter2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Lock 来同步代码
 */
public class Test_2_5 {

	public static void main(String[] args) {
		PrintQueue queue = new PrintQueue();
		Thread[] threads = new Thread[10];
		for(int i=0;i<10;i++){
			threads[i] = new Thread(new Job(queue),"Thread"+i);
		}
		for(int i=0;i<10;i++){
			threads[i].start();
		}
	}
}

class PrintQueue {
	private final Lock lock = new ReentrantLock();
	
	public void printJob(Object document){
		lock.lock();
		
		try {
			long duration = (long) (Math.random()*10000);
			System.out.println(Thread.currentThread().getName()+": PrintQueue: Printing a Job during "+duration/1000+" seconds");
			TimeUnit.MILLISECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
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
		System.out.println(Thread.currentThread().getName()+": Going to print a document");
		queue.printJob(new Object());
		System.out.println(Thread.currentThread().getName()+": The document has bean finished");
	}
	
}
