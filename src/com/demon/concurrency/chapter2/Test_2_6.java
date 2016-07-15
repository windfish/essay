package com.demon.concurrency.chapter2;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <pre>
 * 使用读写锁
 * ReadWriteLock接口和实现类ReentrantReadWriteLock
 *   这个类里有两个锁，一个读操作锁，一个写操作锁。
 *   使用读锁时，允许多个线程同时访问；但使用写锁时，只允许一个线程进行；在执行写操作时，其他线程不能进行读操作
 * </pre>
 */
public class Test_2_6 {

	public static void main(String[] args) {
		PricesInfo pricesInfo = new PricesInfo();
		Reader[] readers = new Reader[5];
		Thread[] readerThreads = new Thread[5];
		for(int i=0;i<5;i++){
			readers[i] = new Reader(pricesInfo);
			readerThreads[i] = new Thread(readers[i]);
		}
		
		Write write = new Write(pricesInfo);
		Thread writeThread = new Thread(write);
		
		for(int i=0;i<5;i++){
			readerThreads[i].start();
		}
		writeThread.start();
		
	}
}

class PricesInfo {
	private double price1;
	private double price2;
	private ReentrantReadWriteLock lock;
	
	public PricesInfo() {
		super();
		this.price1 = 1.0;
		this.price2 = 2.0;
		this.lock = new ReentrantReadWriteLock();
	}

	public double getPrice1() {
		lock.readLock().lock();
		double value = price1;
		lock.readLock().unlock();
		return value;
	}

	public void setPrice1(double price1) {
		lock.writeLock().lock();
		this.price1 = price1;
		lock.writeLock().unlock();
	}

	public double getPrice2() {
		lock.readLock().lock();
		double value = price2;
		lock.readLock().unlock();
		return value;
	}

	public void setPrice2(double price2) {
		lock.writeLock().lock();
		this.price2 = price2;
		lock.writeLock().unlock();
	}
	
	public void setPrices(double price1, double price2) {
		lock.writeLock().lock();
		this.price1 = price1;
		this.price2 = price2;
		lock.writeLock().unlock();
	}
	
}

class Reader implements Runnable {

	private PricesInfo pricesInfo;
	
	public Reader(PricesInfo pricesInfo) {
		super();
		this.pricesInfo = pricesInfo;
	}

	@Override
	public void run() {
		for(int i=0;i<10;i++){
			System.out.println(Thread.currentThread().getName()+": Price 1: "+pricesInfo.getPrice1());
			System.out.println(Thread.currentThread().getName()+": Price 2: "+pricesInfo.getPrice2());
		}
	}
	
}

class Write implements Runnable {

	private PricesInfo pricesInfo;
	
	public Write(PricesInfo pricesInfo) {
		super();
		this.pricesInfo = pricesInfo;
	}

	@Override
	public void run() {
		for(int i=0;i<3;i++){
			System.out.println("Write: Attempt to midify the process.");
			pricesInfo.setPrices(Math.random()*100, Math.random()*100);
			System.out.println("Write: Prices have bean modified.");
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
