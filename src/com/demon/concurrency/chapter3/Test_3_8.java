package com.demon.concurrency.chapter3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Exchanger 数据交换
 * @author fish
 * @version 2016年7月19日 上午10:27:11
 */
public class Test_3_8 {

	/**
	 * 一对一的生产者-消费者的问题
	 */
	public static void main(String[] args) {
		List<String> buffer1 = new ArrayList<String>();
		List<String> buffer2 = new ArrayList<String>();
		
		Exchanger<List<String>> exchanger = new Exchanger<List<String>>();
		
		Producer producer = new Producer(buffer1, exchanger);
		Consumer consumer = new Consumer(buffer2, exchanger);
		
		Thread t1 = new Thread(producer);
		Thread t2 = new Thread(consumer);
		t1.start();
		t2.start();
	}

}

class Producer implements Runnable {
	//待交换的数据结构
	private List<String> buffer;
	private final Exchanger<List<String>> exchanger;

	public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
		super();
		this.buffer = buffer;
		this.exchanger = exchanger;
	}

	@Override
	public void run() {
		int cycle = 1;
		for(int i=0;i<10;i++){
			System.out.println("Producer: Cycle "+cycle);
			for(int j=0;j<10;j++){
				String message  = "Event "+((i*10)+j);
				System.out.println("Producer: "+message);
				buffer.add(message);
			}
			try {
				buffer = exchanger.exchange(buffer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Producer: "+buffer.size());
			cycle++;
		}
	}
	
}

class Consumer implements Runnable {
	private List<String> buffer;
	private final Exchanger<List<String>> exchanger;

	public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
		super();
		this.buffer = buffer;
		this.exchanger = exchanger;
	}

	@Override
	public void run() {
		int cycle = 1;
		for(int i=0;i<10;i++){
			System.out.println("Consumer: Cycle "+cycle);
			try {
				buffer = exchanger.exchange(buffer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Consumer: "+buffer.size());
			for(int j=0;j<10;j++){
				String message = buffer.get(0);
				System.out.println("Consumer: "+message);
				buffer.remove(0);
			}
			cycle++;
		}
	}
	
}
