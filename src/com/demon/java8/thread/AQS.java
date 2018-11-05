package com.demon.java8.thread;

import java.io.Serializable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * <pre>
 * AQS 支持两种同步方式：1、独占式  2、共享式
 * 独占式如 ReentrantLock，共享式如 Semaphore、CountDownLatch，组合式如 ReentrantReadWriteLock
 * 
 * 同步器的设计基于模板方法模式的，使用方式：
 * 1、使用者继承 AbstractQueuedSynchronizer 并重写指定的方法，即对共享资源status 的 acquire/release
 * 2、将AQS 组合在自定义同步组件的实现中，并调用模板方法，而这些模板方法会调用使用者重写的方法
 * 
 * 可重写的方法：
 * protected boolean tryAcquire(int arg)：独占式获取同步状态，试着获取，成功返回true，失败返回false
 * protected boolean tryRelease(int arg)：独占式释放同步状态，等待中的其他线程此时将有机会获取到同步状态
 * protected int tryAcquireShared(int arg)：共享式获取同步状态，返回值大于0，代表获取成功；否则获取失败
 * protected boolean tryReleaseShared(int arg)：共享式释放同步状态，成功为true，失败为false
 * protected boolean isHeldExclusively()：是否在独占模式下被线程使用
 * </pre>
 * 
 * @author xuliang
 * @since 2018年11月5日 下午9:34:24
 *
 */
public class AQS implements Serializable {

	private static final long serialVersionUID = 7677370200333814858L;

	private static class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = -6331303868028542001L;
		/**
		 * 是否处于占用状态
		 */
		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}
		/**
		 * 当状态为0时获取锁，CAS 操作成功，status置为1
		 */
		@Override
		protected boolean tryAcquire(int acquires) {
			assert acquires == 1;
			if(compareAndSetState(0, 1)){
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}
		/**
		 * 释放锁，将同步状态置为0
		 */
		@Override
		protected boolean tryRelease(int releases) {
			assert releases == 1;
			if(getState() == 0){
				throw new IllegalMonitorStateException();
			}
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}
	}
	
	private final Sync sync = new Sync();
	
	/**
	 * 加锁
	 */
	public void lock(){
		sync.acquire(1);
	}
	
	public boolean tryLock(){
		return sync.tryAcquire(1);
	}
	
	/**
	 * 解锁
	 */
	public void unlock(){
		sync.release(1);
	}
	
	public boolean isLock(){
		return sync.isHeldExclusively();
	}
	
	private static CyclicBarrier barrier = new CyclicBarrier(31);
	private static int a = 0;
	private static AQS aqs = new AQS();
	public static void main(String[] args) {
		for(int i=0;i<30;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i=0;i<1000;i++){
						a++;
					}
					try {
						barrier.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e1) {
			e1.printStackTrace();
		}
		System.out.println("未加锁，a="+a);
		
		barrier.reset();
		a=0;
		for(int i=0;i<30;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i=0;i<1000;i++){
						aqs.lock();
						a++;
						aqs.unlock();
					}
					try {
						barrier.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e1) {
			e1.printStackTrace();
		}
		System.out.println("加锁，a="+a);
	}
	
}
