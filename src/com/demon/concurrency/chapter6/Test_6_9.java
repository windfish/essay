package com.demon.concurrency.chapter6;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 使用原子数组
 * @author fish
 * @version 2016年8月12日 下午4:22:02
 */
public class Test_6_9 {

	public static void main(String[] args) {
		final int THREADS = 100;
		AtomicIntegerArray vector = new AtomicIntegerArray(1000);
		Incrementer incrementer = new Incrementer(vector);
		Decrementer decrementer = new Decrementer(vector);
		
		Thread incrementerThreads[] = new Thread[THREADS];
		Thread decrementerThreads[] = new Thread[THREADS];
		for(int i=0;i<THREADS;i++){
			incrementerThreads[i] = new Thread(incrementer);
			decrementerThreads[i] = new Thread(decrementer);
			
			incrementerThreads[i].start();
			decrementerThreads[i].start();
		}
		
		for(int i=0;i<THREADS;i++){
			try {
				incrementerThreads[i].join();
				decrementerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<vector.length();i++){
			if(vector.get(i)!=0){
				System.out.println("Vector["+i+"]: "+vector.get(i));
			}
		}
		System.out.println("Main: End");
	}

}

class Incrementer implements Runnable {
	private AtomicIntegerArray vector;

	public Incrementer(AtomicIntegerArray vector) {
		super();
		this.vector = vector;
	}

	@Override
	public void run() {
		for(int i=0;i<vector.length();i++){
			vector.getAndIncrement(i);
		}
	}
}

class Decrementer implements Runnable{
	private AtomicIntegerArray vector;

	public Decrementer(AtomicIntegerArray vector) {
		super();
		this.vector = vector;
	}

	@Override
	public void run() {
		for(int i=0;i<vector.length();i++){
			vector.getAndDecrement(i);
		}
	}
	
}
