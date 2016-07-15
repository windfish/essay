package com.demon.concurrency.chapter2;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 在锁中使用多条件
 * 一个锁可能关联一个或多个条件，这些条件通过Condition 接口声明
 * 目的是允许线程获得锁并且查看等待的一个条件是否满足，如果不满足，就挂起直到某个线程唤醒它们
 * Condition 接口提供了挂起线程和唤醒线程的机制
 */
public class Test_2_8 {

	public static void main(String[] args) {
		FileMock mock = new FileMock(100, 10);
		Buffer8 buffer = new Buffer8(20);
		
		Producer producer = new Producer(mock, buffer);
		Thread p = new Thread(producer);
		Consumer consumers[] = new Consumer[3];
		Thread c[] = new Thread[3];
		for(int i=0;i<3;i++){
			consumers[i] = new Consumer(buffer);
			c[i] = new Thread(consumers[i],"Consumer #"+i);
		}
		
		p.start();
		for(int i=0;i<3;i++){
			c[i].start();
		}
		
	}
}

/**
 * 文本文件模拟类
 */
class FileMock {
	private String content[];
	private int index;
	
	public FileMock(int size, int length) {
		content = new String[size];
		for(int i=0;i<size;i++){
			StringBuilder buffer = new StringBuilder(length);
			for(int j=0;j<length;j++){
				int tmp = (int) (Math.random()*255);
				buffer.append((char)tmp);
				content[i] = buffer.toString();
			}
			index = 0;
		}
	}
	
	/**
	 * 如果有文件处理，返回true，否则返回false
	 */
	public boolean hasMoreLines(){
		return index<content.length;
	}
	
	/**
	 * 返回index指向的行内容，并将index加1
	 */
	public String getLines(){
		if(hasMoreLines()){
			System.out.println("Mock: "+(content.length-index));
			return content[index++];
		}
		return null;
	}
	
}

/**
 * 缓冲类，消费者和生产者共享
 */
class Buffer8{
	private LinkedList<String> buffer; //存放共享数据
	private int maxSize; //存放buffer的长度
	private ReentrantLock lock;
	private Condition lines; //锁的条件
	private Condition space; //锁的条件
	private boolean pendingLines; //用来表明缓冲区是否还有数据
	
	public Buffer8(int maxSize) {
		this.maxSize = maxSize;
		this.buffer = new LinkedList<String>();
		this.lock = new ReentrantLock();
		this.lines = this.lock.newCondition();
		this.space = this.lock.newCondition();
		this.pendingLines = true;
	}
	
	/**
	 * <pre>
	 * 将字符串写入缓冲区
	 * 写入之前会判断缓冲区是否有位置，若缓冲区没有空位，调用条件space 的await()等待空位出现；
	 * 当其他线程调用space 的signal()或signalAll()时，这个线程将被唤醒
	 * </pre>
	 */
	public void insert(String line){
		lock.lock();
		try {
			while(buffer.size() == maxSize){
				space.await(); //等待缓冲区空位的出现
			}
			buffer.offer(line);
			System.out.println(Thread.currentThread().getName()+": Inserted line: "+buffer.size());
			lines.signalAll(); //通知缓冲区已有数据
		} catch(InterruptedException e){
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * <pre>
	 * 从缓冲区中取字符串
	 * 取之前会判断缓冲区中是否有数据，若没有数据，则调用条件lines 的await()等待数据的出现
	 * 当其他线程调用lines 的signal()或signalAll()时，这个线程将被唤醒
	 * </pre>
	 */
	public String get(){
		String line = null;
		lock.lock();
		try{
			while(buffer.size()==0 && hasPendingLines()){
				lines.await(); //等待数据的出现
			}
			if(hasPendingLines()){
				line = buffer.poll();
				System.out.println(Thread.currentThread().getName()+": Line Readed: "+buffer.size());
				space.signalAll(); //通知缓冲区有空位
			}
		} catch(InterruptedException e){
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return line;
	}
	
	/**
	 * 当生产者不再产生数据时调用
	 */
	public void setPendingLines(boolean pendingLines){
		this.pendingLines = pendingLines;
	}
	
	public boolean hasPendingLines(){
		return pendingLines || buffer.size()>0;
	}
	
}

/**
 * 生产者
 */
class Producer implements Runnable {

	private FileMock mock;
	private Buffer8 buffer;
	
	public Producer(FileMock mock, Buffer8 buffer) {
		super();
		this.mock = mock;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		buffer.setPendingLines(true); //开始写入数据
		while(mock.hasMoreLines()){
			String line = mock.getLines();
			buffer.insert(line);
		}
		buffer.setPendingLines(false); //停止写入数据
	}
	
}

class Consumer implements Runnable {
	private Buffer8 buffer;
	
	public Consumer(Buffer8 buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public void run() {
		while(buffer.hasPendingLines()){
			String line = buffer.get();
			processLine(line);
		}
	}
	
	public void processLine(String line){
		System.out.println(line);
		try {
			Thread.sleep(new java.util.Random().nextInt(100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
