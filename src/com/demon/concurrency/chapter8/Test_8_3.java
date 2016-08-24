package com.demon.concurrency.chapter8;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 监控Phaser 类
 * @author fish
 * @version 2016年8月24日 下午2:47:29
 */
public class Test_8_3 {

	public static void main(String[] args) {
		Phaser phaser = new Phaser(3);
		for(int i=0;i<3;i++){
			Task3 task = new Task3(i+1, phaser);
			new Thread(task).start();
		}
		for(int i=0;i<10;i++){
			System.out.println("************************");
			System.out.println("Main: Phaser Log");
			//返回phaser对象的当前阶段
			System.out.println("Main: Phaser: phase: "+phaser.getPhase());
			//返回使用phaser对象作为同步机制的任务数
			System.out.println("Main: Phaser: Registered Parties: "+phaser.getRegisteredParties());
			//返回在一个阶段结束时已到达的任务数
			System.out.println("Main: Phaser: Arrived Parties: "+phaser.getArrivedParties());
			//返回在一个阶段结束时未到达的任务数
			System.out.println("Main: Phaser: Unarrived Parties: "+phaser.getUnarrivedParties());
			System.out.println("************************");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

class Task3 implements Runnable {
	private int time;
	private Phaser phaser;

	public Task3(int time, Phaser phaser) {
		super();
		this.time = time;
		this.phaser = phaser;
	}

	@Override
	public void run() {
		phaser.arrive(); //第一阶段开始
		System.out.println(Thread.currentThread().getName()+": Entering phaser 1.");
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+": Finishing phaser 1.");
		phaser.arriveAndAwaitAdvance(); //第一阶段结束并等待其他线程
		
		//第二阶段开始
		System.out.println(Thread.currentThread().getName()+": Entering phaser 2.");
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+": Finishing phaser 2.");
		phaser.arriveAndAwaitAdvance(); //第二阶段结束并等待其他线程
		
		//第三阶段开始
		System.out.println(Thread.currentThread().getName()+": Entering phaser 3.");
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+": Finishing phaser 3.");
		phaser.arriveAndDeregister(); //第三阶段结束
	}
	
}
