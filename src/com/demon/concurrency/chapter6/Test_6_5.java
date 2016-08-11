package com.demon.concurrency.chapter6;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 使用带有延迟元素的线程安全列表  DelayQueue
 * @author fish
 * @version 2016年8月11日 下午3:16:32
 */
public class Test_6_5 {

	public static void main(String[] args) {
		DelayQueue<Event5> queue = new DelayQueue<Event5>();
		Thread threads[] = new Thread[5];
		for(int i=0;i<threads.length;i++){
			threads[i] = new Thread(new Task5(i+1, queue));
		}
		for(int i=0;i<threads.length;i++){
			threads[i].start();
		}
		for(int i=0;i<threads.length;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		do{
			int counter = 0;
			Event5 event = null;
			do{
				event = queue.poll();
				if(event != null){
					counter++;
				}
			}while(event!=null);
			System.out.printf("At %s you have read %d events\n",new Date(),counter);
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(queue.size()>0);
	}

}

class Event5 implements Delayed {
	private Date startDate;

	public Event5(Date startDate) {
		super();
		this.startDate = startDate;
	}

	@Override
	public int compareTo(Delayed o) {
		long result = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
		if(result<0){
			return -1;
		}else if(result>0){
			return 1;
		}
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		Date now = new Date();
		long diff = startDate.getTime() - now.getTime();
		return unit.convert(diff, TimeUnit.NANOSECONDS);
	}
	
}

class Task5 implements Runnable {
	private int id;
	private DelayQueue<Event5> queue;

	public Task5(int id, DelayQueue<Event5> queue) {
		super();
		this.id = id;
		this.queue = queue;
	}

	/*
	 * 计算要创建的event 的激活日期，用实际日期加上task的编号对应的秒数
	 */
	@Override
	public void run() {
		Date now = new Date();
		Date delay = new Date();
		delay.setTime(now.getTime()+(id*1000));
		System.out.println("Thread "+id+": "+delay);
		for(int i=0;i<100;i++){
			Event5 event = new Event5(delay);
			queue.add(event);
		}
	}
	
}
