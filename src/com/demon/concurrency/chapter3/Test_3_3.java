package com.demon.concurrency.chapter3;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对一个资源的多个副本进行访问控制
 */
public class Test_3_3 {

	public static void main(String[] args) {
		//一个打印队列，被三个打印机使用
		PrintQueue3 queue = new PrintQueue3();
		Thread threads[] = new Thread[10];
		for(int i=0;i<10;i++){
			threads[i] = new Thread(new Job3(queue),"Thread #"+i);
		}
		for(int i=0;i<10;i++){
			threads[i].start();
		}
	}

}

class PrintQueue3 {
	private final Semaphore semaphore;
	private boolean freePrinters[]; //存放打印机状态
	private Lock lockPrinters;
	
	public PrintQueue3() {
		semaphore = new Semaphore(3);
		freePrinters = new boolean[3];
		for(int i=0;i<3;i++){
			freePrinters[i] = true; //打印机空闲
		}
		lockPrinters = new ReentrantLock();
	}
	
	public void printJob(Object document){
		try {
			semaphore.acquire(); //获得信号量
			int assignedPriter = getPrinter();
			long duration = (long) (Math.random()*10);
			System.out.println(Thread.currentThread().getName()+": PrintQueue: Printing in Printer #"+assignedPriter+" Job during "+duration+" seconds");
			TimeUnit.SECONDS.sleep(duration);
			freePrinters[assignedPriter] = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			semaphore.release(); //释放信号量
		}
	}
	
	//获取空闲的打印机并指定其工作
	private int getPrinter(){
		int ret = -1;
		try {
			lockPrinters.lock();
			for(int i=0;i<freePrinters.length;i++){
				if(freePrinters[i]){
					ret = i;
					freePrinters[i] = false;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lockPrinters.unlock();
		}
		return ret;
	}
	
}

class Job3 implements Runnable {

	private PrintQueue3 queue;
	
	public Job3(PrintQueue3 queue) {
		this.queue = queue;
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+": Going to print a job");
		queue.printJob(new Object());
		System.out.println(Thread.currentThread().getName()+": The document has bean done");
	}
	
}
