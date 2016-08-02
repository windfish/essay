package com.demon.concurrency.chapter6;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 使用非阻塞式线程安全列表 ConcurrentLinkedDeque
 * @author fish
 * @version 2016年8月2日 下午2:24:53
 */
public class Test_6_2 {

	public static void main(String[] args) {
		ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<String>();
		Thread threads[] = new Thread[100];
		for(int i=0;i<threads.length;i++){
			AddTask addTask = new AddTask(list);
			threads[i] = new Thread(addTask);
			threads[i].start();
		}
		System.out.println("Main: "+threads.length+" AddTask threads have been launched");
		for(int i=0;i<threads.length;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Main: Size of the list: "+list.size());
		
		for(int i=0;i<threads.length;i++){
			PollTask pollTask = new PollTask(list);
			threads[i] = new Thread(pollTask);
			threads[i].start();
		}
		System.out.println("Main: "+threads.length+" PollTask threads have been launched");
		for(int i=0;i<threads.length;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Main: Size of the list: "+list.size());
	}

}

class AddTask implements Runnable {
	private ConcurrentLinkedDeque<String> list;

	public AddTask(ConcurrentLinkedDeque<String> list) {
		super();
		this.list = list;
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		for(int i=0;i<10000;i++){
			list.add(name+": Element "+i);
		}
	}
	
}

class PollTask implements Runnable {
	private ConcurrentLinkedDeque<String> list;

	public PollTask(ConcurrentLinkedDeque<String> list) {
		super();
		this.list = list;
	}

	@Override
	public void run() {
		for(int i=0;i<5000;i++){
			list.pollFirst(); //返回并移除列表中第一个元素
			list.pollLast(); //返回并移除列表中最后一个元素
		}
	}
	
}
