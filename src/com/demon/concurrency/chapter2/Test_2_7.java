package com.demon.concurrency.chapter2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 * 修改锁的公平性
 * ReentrantLock 和ReentrantReadWriteLock 类的构造器都有一个布尔参数fair
 * 默认值是false，表示非公平模式（Non-Fair Mode）
 *   非公平模式下，当有多个线程在等待锁时，锁将选择他们中的一个来访问临界区，这个选择是没有约束的，随机的
 * 若fair值是true，表示公平模式（Fair Mode）
 *   公平模式下，当有很多线程在等待锁时，锁将选择他们中的一个来访问临界区，而且选择的是等待时间最长的
 * 
 * 这两种模式只影响lock()和unlock()，tryLock()没有将线程置于休眠，fair属性并不影响这个方法
 * </pre>
 */
public class Test_2_7 {

	public static void main(String[] args) {
		PrintQueue7 queue = new PrintQueue7();
		Thread[] threads = new Thread[10];
		for(int i=0;i<10;i++){
			threads[i] = new Thread(new Job7(queue),"Thread"+i);
		}
		for(int i=0;i<10;i++){
			threads[i].start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class PrintQueue7 {
	private final Lock lock = new ReentrantLock(false);
	
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

class Job7 implements Runnable {
	private PrintQueue7 queue;
	
	public Job7(PrintQueue7 queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+": Going to print a document");
		queue.printJob(new Object());
		System.out.println(Thread.currentThread().getName()+": The document has bean finished");
	}
	
}
