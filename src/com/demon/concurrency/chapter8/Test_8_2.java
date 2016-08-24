package com.demon.concurrency.chapter8;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 监控Lock 接口
 * @author fish
 * @version 2016年8月24日 上午11:31:34
 */
public class Test_8_2 {

	public static void main(String[] args) {
		MyLock lock = new MyLock();
		Thread threads[] = new Thread[5];
		for(int i=0;i<5;i++){
			threads[i] = new Thread(new Task2(lock));
			threads[i].start();
		}
		for(int i=0;i<15;i++){
			System.out.println("Main: Logging the lock.");
			System.out.println("***********************");
			//输出获得锁的线程名
			System.out.println("Lock: Owner: "+lock.getOwnerName());
			//输出是否有等待获得锁的线程
			System.out.println("Lock: Queue Threads: "+lock.hasQueuedThreads());
			if(lock.hasQueuedThreads()){
				//输出等待获得锁的线程队列的长度
				System.out.println("Lock: Queue Length: "+lock.getQueueLength());
				//输出具体等待获得锁的线程名
				System.out.print("Lock: Queue Threads:");
				Collection<Thread> lockThreads = lock.getThreads();
				for(Thread t: lockThreads){
					System.out.print(t.getName()+" ");
				}
				System.out.println("");
			}
			//输出锁是否是公平模式
			System.out.println("Lock: Fairness: "+lock.isFair());
			//输出锁是否是否被一个线程占有
			System.out.println("Lock: Locked: "+lock.isLocked());
			System.out.println("***********************");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

class MyLock extends ReentrantLock {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 返回当前获得锁的线程名字
	 */
	public String getOwnerName(){
		if(this.getOwner() == null){
			return "None";
		}
		return this.getOwner().getName();
	}
	
	/**
	 * 返回正在等待获取锁的线程列表
	 */
	public Collection<Thread> getThreads(){
		return this.getQueuedThreads();
	}
	
}

class Task2 implements Runnable {
	private Lock lock;

	public Task2(Lock lock) {
		super();
		this.lock = lock;
	}

	@Override
	public void run() {
		for(int i=0;i<5;i++){
			lock.lock();
			System.out.println(Thread.currentThread().getName()+": Get the lock.");
			try {
				TimeUnit.MILLISECONDS.sleep(500);
				System.out.println(Thread.currentThread().getName()+": Free the lock.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				lock.unlock();
			}
		}
	}
	
}
