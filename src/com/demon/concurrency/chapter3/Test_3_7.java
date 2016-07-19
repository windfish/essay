package com.demon.concurrency.chapter3;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 在阶段切换时，执行一些额外的操作，覆盖onAdvance()方法
 */
public class Test_3_7 {

	public static void main(String[] args) {
		MyPhaser phaser = new MyPhaser();
		Student students[] = new Student[5];
		for(int i=0;i<students.length;i++){
			students[i] = new Student(phaser);
			phaser.register();
		}
		Thread threads[] = new Thread[students.length];
		for(int i=0;i<students.length;i++){
			threads[i] = new Thread(students[i]);
			threads[i].start();
		}
		for(int i=0;i<threads.length;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Main: The phaser has finished: "+phaser.isTerminated());
	}

}

class MyPhaser extends Phaser {
	/**
	 * 在阶段改变时，执行一些额外的操作
	 * 根据传入的phase 不同，做不同的操作
	 */
	@Override
	protected boolean onAdvance(int phase, int registeredParties) {
		switch (phase) {
		case 0:
			return studentArrived();
		case 1:
			return finishFirstExercise();
		case 2:
			return finishSecondExercise();
		case 3:
			return finishExercise();
		default:
			return true;
		}
	}
	
	private boolean studentArrived(){
		System.out.println("Phaser: The exam are going to start.The student are ready.");
		System.out.println("Phaser: We have "+getRegisteredParties()+" students.");
		return false;
	}
	
	private boolean finishFirstExercise(){
		System.out.println("Phaser: All students have finished the first exercise.");
		System.out.println("Phaser: It's time for second one.");
		return false;
	}
	
	private boolean finishSecondExercise(){
		System.out.println("Phaser: All students have finished the second exercise.");
		System.out.println("Phaser: It's time for third one.");
		return false;
	}
	
	private boolean finishExercise(){
		System.out.println("Phaser: All students have finished the final exercise.");
		System.out.println("Phaser: Thank you for your time.");
		return true;
	}
}

class Student implements Runnable {
	private Phaser phaser;

	public Student(Phaser phaser) {
		super();
		this.phaser = phaser;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+": He arrived to do the exam. "+new Date());
		phaser.arriveAndAwaitAdvance();
		
		System.out.println(Thread.currentThread().getName()+": Is going to do first exercise. "+new Date());
		doExercise1();
		System.out.println(Thread.currentThread().getName()+": He finish the first exercise. "+new Date());
		phaser.arriveAndAwaitAdvance();
		
		System.out.println(Thread.currentThread().getName()+": Is going to do second exercise. "+new Date());
		doExercise2();
		System.out.println(Thread.currentThread().getName()+": He finish the second exercise. "+new Date());
		phaser.arriveAndAwaitAdvance();
		
		System.out.println(Thread.currentThread().getName()+": Is going to do third exercise. "+new Date());
		doExercise3();
		System.out.println(Thread.currentThread().getName()+": He finish the exam. "+new Date());
		phaser.arriveAndAwaitAdvance();
	}
	
	private void doExercise1(){
		long duration = (long) (Math.random()*10);
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void doExercise2(){
		long duration = (long) (Math.random()*10);
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void doExercise3(){
		long duration = (long) (Math.random()*10);
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
