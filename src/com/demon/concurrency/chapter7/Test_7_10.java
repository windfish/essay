package com.demon.concurrency.chapter7;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现基于优先级的传输队列
 * @author fish
 * @version 2016年8月23日 下午3:21:30
 */
public class Test_7_10 {

	/*
	 * 生产者、消费者问题，按优先级消费数据
	 */
	public static void main(String[] args) throws InterruptedException {
		MyPriorityBlockingQueue<Event> buffer = new MyPriorityBlockingQueue<>();
		
		Producer producer = new Producer(buffer);
		Thread producers[] = new Thread[10];
		for(int i=0;i<producers.length;i++){
			producers[i] = new Thread(producer);
			producers[i].start();
		}
		
		Consumer consumer = new Consumer(buffer);
		Thread consumerThread = new Thread(consumer);
		consumerThread.start();
		
		//打印实际的消费者数
		System.out.println("Main: Buffer: Consumer count: "+buffer.getWaitingConsumerCount());
		
		//传递一个事件给消费者
		Event event1 = new Event("Core Event 1", 0);
		buffer.transfer(event1);
		System.out.println("Main: My Event has bean transfered.");
		
		for(int i=0;i<producers.length;i++){
			producers[i].join();
		}
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Main: Buffer: Consumer count: "+buffer.getWaitingConsumerCount());
		
		//传递另一个事件
		Event event2 = new Event("Core Event 2", 0);
		buffer.transfer(event2);
		
		consumerThread.join();
		System.out.println("Main: End.");
	}

}

class MyPriorityBlockingQueue<E> extends PriorityBlockingQueue<E> implements TransferQueue<E> {

	private static final long serialVersionUID = 1L;
	private AtomicInteger counter; //存储等待消费元素的消费者数量
	private LinkedBlockingQueue<E> transfered; //存储已传递但尚未被消费的元素
	private ReentrantLock lock;
	
	public MyPriorityBlockingQueue() {
		counter = new AtomicInteger(0);
		transfered = new LinkedBlockingQueue<E>();
		lock = new ReentrantLock();
	}

	/**
	 * 尝试立即将元素发送给一个正在等待的消费者。
	 * 如果没有消费者，该方法返回false
	 */
	@Override
	public boolean tryTransfer(E e) {
		lock.lock();
		boolean value;
		if(counter.get() == 0){
			value = false;
		}else{
			put(e);
			value = true;
		}
		lock.unlock();
		return value;
	}

	/**
	 * 尝试立即将元素发送给一个正在等待的消费者。
	 * 如果没有消费者，该方法将元素存储到transfered 队列，并等待出现试图获取元素的第一个消费者，在这之前，线程将被阻塞
	 */
	@Override
	public void transfer(E e) throws InterruptedException {
		lock.lock();
		if(counter.get() != 0){
			put(e);
			lock.unlock();
		}else{
			transfered.add(e);
			lock.unlock();
			synchronized (e) {
				e.wait();
			}
		}
	}

	/**
	 * e 表示生产和消费的元素；timeout 表示如果没有消费者则等待一个消费者的时间；unit 表示等待时间的单位。
	 * 如果消费者在等待，则立即发送元素；否则，将参数转换为毫秒并使用wait()让线程休眠。
	 * 当消费者取走元素时，如果线程还在休眠，则使用notify()唤醒。
	 */
	@Override
	public boolean tryTransfer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		lock.lock();
		if(counter.get() != 0){
			put(e);
			lock.unlock();
			return true;
		}else{
			transfered.add(e);
			long newTimeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
			lock.unlock();
			e.wait(newTimeout);
			lock.lock();
			if(transfered.contains(e)){
				//等待timeout的时间，没有等到消费者
				transfered.remove(e);
				lock.unlock();
				return false;
			}else{
				//等到消费者
				lock.unlock();
				return true;
			}
		}
	}

	/**
	 * 是否有等待的消费者
	 */
	@Override
	public boolean hasWaitingConsumer() {
		return counter.get()!=0;
	}

	/**
	 * 有多少个等待的消费者
	 */
	@Override
	public int getWaitingConsumerCount() {
		return counter.get();
	}
	
	/**
	 * 返回下一个要被消费的元素，如果transfered 中有元素，那么就从这个列表中获取；否则从优先队列中获取。
	 * 该方法将被准备消费元素的消费者调用，增加等待的消费者数量。
	 * 1、先获取锁，然后增加等待的消费者数量；
	 * 2、如果transfered 中没有元素，则让线程休眠直到有元素可被消费
	 * 3、若transfered有元素，则取出元素，并唤醒可能在等待元素被消费的线程
	 * 4、减少正在等待的消费者的数量并释放锁，返回消费的元素
	 */
	@Override
	public E take() throws InterruptedException {
		lock.lock();
		counter.incrementAndGet();
		
		E value = transfered.poll();
		if(value == null){
			lock.unlock();
			value = super.take();
			lock.lock();
		}else{
			synchronized (value) {
				value.notify();
			}
		}
		counter.decrementAndGet();
		lock.unlock();
		return value;
	}
	
}

class Event implements Comparable<Event> {
	private String thread;
	private int priority;

	public Event(String thread, int priority) {
		super();
		this.thread = thread;
		this.priority = priority;
	}
	
	public String getThread() {
		return thread;
	}
	
	public int getPriority() {
		return priority;
	}

	@Override
	public int compareTo(Event o) {
		if(this.priority > o.getPriority()){
			return -1;
		}else if(this.priority < o.getPriority()){
			return 1;
		}else{
			return 0;
		}
	}
	
}

class Producer implements Runnable {
	private MyPriorityBlockingQueue<Event> buffer;

	public Producer(MyPriorityBlockingQueue<Event> buffer) {
		super();
		this.buffer = buffer;
	}

	@Override
	public void run() {
		for(int i=0;i<100;i++){
			Event event = new Event(Thread.currentThread().getName(), i);
			buffer.put(event);
		}
	}
	
}

class Consumer implements Runnable {
	private MyPriorityBlockingQueue<Event> buffer;

	public Consumer(MyPriorityBlockingQueue<Event> buffer) {
		super();
		this.buffer = buffer;
	}

	@Override
	public void run() {
		for(int i=0;i<1002;i++){
			try {
				Event value = buffer.take();
				System.out.printf("Consumer: %s: %d\n",value.getThread(),value.getPriority());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
