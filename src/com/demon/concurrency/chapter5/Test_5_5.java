package com.demon.concurrency.chapter5;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * 在任务中抛出异常
 * @author fish
 * @version 2016年8月1日 下午4:57:20
 */
public class Test_5_5 {

	public static void main(String[] args) {
		int array[] = new int[100];
		Task5 task = new Task5(array, 0, 100);
		ForkJoinPool pool = new ForkJoinPool();
		pool.execute(task);
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(task.isCompletedAbnormally()){ //任务是否异常结束
			System.out.println("Main: An exception has occured");
			System.out.println("Main: "+task.getException());
		}
		System.out.println("Main: Result: "+task.join());
	}

}

class Task5 extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;
	private int array[];
	private int start, end;

	public Task5(int[] array, int start, int end) {
		super();
		this.array = array;
		this.start = start;
		this.end = end;
	}

	@Override
	protected Integer compute() {
		System.out.println("Task: Start from "+start+" to "+end);
		if(end - start < 10){
			if(start<3 && end>3){
				//碰到第4个元素时，抛出异常，会导致抛出异常的任务及其父任务都是异常结束
				throw new RuntimeException("This task throws an Exception: Task from "+start+" to "+end);
			}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			int mid = (start+end)/2;
			Task5 t1 = new Task5(array, start, mid);
			Task5 t2 = new Task5(array, mid, end);
			invokeAll(t1,t2);
		}
		System.out.println("Task: End from "+start+" to "+end);
		return 0;
	}
	
}
