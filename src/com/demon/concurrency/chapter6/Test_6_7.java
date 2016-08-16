package com.demon.concurrency.chapter6;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 生成并发随机数  ThreadLocalRandom
 * @author fish
 * @version 2016年8月12日 上午11:16:02
 */
public class Test_6_7 {

	public static void main(String[] args) {
		Thread threads[] = new Thread[3];
		for(int i=0;i<3;i++){
			TaskLocalRandom task = new TaskLocalRandom();
			threads[i] = new Thread(task);
			threads[i].start();
		}
	}

}

class TaskLocalRandom implements Runnable {
	
	public TaskLocalRandom() {
		//初始化随机数生成器
		ThreadLocalRandom.current();
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		for(int i=0;i<10;i++){
			//ThreadLocalRandom.current() 获取与当前线程相关联的随机数生成器；若没有，则重新生成一个
			System.out.println(name+": "+ThreadLocalRandom.current().nextInt(10));
		}
	}
	
}
