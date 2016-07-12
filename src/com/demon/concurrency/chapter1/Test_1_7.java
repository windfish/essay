package com.demon.concurrency.chapter1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 使用线程初始化资源，等待线程终止再执行其他任务
 * 
 * 使用Thread 的join()方法：当一个线程对象的join()方法被调用时，调用它的线程将被挂起，直到这个线程对象完成它的任务
 */
public class Test_1_7 {

	public static void main(String[] args) {
		DataSourcesLoader dsLoader = new DataSourcesLoader();
		Thread thread1 = new Thread(dsLoader, "DataSourcesLoader");
		
		NetworkConnectionsLoader ncLoader =  new NetworkConnectionsLoader();
		Thread thread2 = new Thread(ncLoader, "NetworkConnectionsLoader");
		
		thread1.start();
		thread2.start();
		
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//可以看出，上面两个线程执行完之后，主线程(main方法)才会继续执行
		System.out.printf("Main：Configuration has been loaded：%s\n", new Date());
	}

}

class DataSourcesLoader implements Runnable {
	@Override
	public void run() {
		System.out.printf("Beginning data sources loading: %s\n", new Date());
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("Data sources loading has finished: %s\n", new Date());
	}
}

class NetworkConnectionsLoader implements Runnable {
	@Override
	public void run() {
		System.out.printf("Beginning NetworkConnections loading: %s\n", new Date());
		try {
			TimeUnit.SECONDS.sleep(6);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("NetworkConnections loading has finished: %s\n", new Date());
	}
}
