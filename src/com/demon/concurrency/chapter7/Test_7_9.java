package com.demon.concurrency.chapter7;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 实现定制Lock 类
 * @author fish
 * @version 2016年8月23日 上午10:53:41
 */
public class Test_7_9 {

	public static void main(String[] args) {
		MyLock lock = new MyLock();
		for(int i=0;i<10;i++){
			Task9 task = new Task9(lock, "Task-"+i);
			new Thread(task).start();
		}
		boolean value = false;
		do{
			try {
				value = lock.tryLock(1,TimeUnit.SECONDS);
				if(!value){
					System.out.println("Main: Trying to get the lock.");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				value = false;
			}
		}while(!value);
		System.out.println("Main: Get the lock.");
		lock.unlock();
		System.out.println("Main: End.");
	}

}

/**
 * AbstractQueuedSynchronizer 类用来实现带有锁或信号特性的同步机制。
 * 提供操作来对临界区代码的访问进行控制，并对等待访问临界区代码的阻塞线程队列进行管理。
 * @author fish
 * @version 2016年8月23日 上午11:14:45
 */
class MyAbstractQueuedSynchronizer extends AbstractQueuedSynchronizer {

	private static final long serialVersionUID = 1L;
	private AtomicInteger state;
	
	public MyAbstractQueuedSynchronizer() {
		state = new AtomicInteger(0);
	}
	
	/**
	 * 访问临界区代码时，调用这个方法。
	 * 把state 的值从0改为1，如果成功返回true，否则返回false
	 */
	@Override
	protected boolean tryAcquire(int arg) {
		//锁可以被获取时，变量值为0；锁不可以用时，变量值为1
		return state.compareAndSet(0, 1);
	}
	
	/**
	 * 释放临界区代码时，调用这个方法。
	 * 把state 的值从1改为0，如果成功返回true，否则返回false
	 */
	@Override
	protected boolean tryRelease(int arg) {
		return state.compareAndSet(1, 0);
	}
	
}

/**
 * 定制锁，可以获得锁的使用情况、控制临界区的锁定时间，还可以实现高级同步机制（例如：在特定时间内才可用的资源进行控制）
 * @author fish
 * @version 2016年8月23日 上午11:46:47
 */
class MyLock implements Lock {
	private AbstractQueuedSynchronizer sync;
	
	public MyLock() {
		sync = new MyAbstractQueuedSynchronizer();
	}

	@Override
	public void lock() {
		sync.acquire(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);
	}

	@Override
	public boolean tryLock() {
		try {
			return sync.tryAcquireNanos(1, 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		return sync.tryAcquireNanos(1, TimeUnit.NANOSECONDS.convert(time, unit));
	}

	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition newCondition() {
		return sync.new ConditionObject();
	}
	
}

class Task9 implements Runnable {
	private MyLock lock;
	private String name;

	public Task9(MyLock lock, String name) {
		super();
		this.lock = lock;
		this.name = name;
	}

	@Override
	public void run() {
		lock.lock();
		System.out.println("Task: "+name+": Take the lock.");
		try {
			TimeUnit.SECONDS.sleep(2);
			System.out.println("Task: "+name+": Free the lock.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	
}
