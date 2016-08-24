package com.demon.concurrency.chapter8;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * 监控Fork/Join 池
 * @author fish
 * @version 2016年8月24日 下午3:39:30
 */
public class Test_8_5 {

	public static void main(String[] args) throws InterruptedException {
		ForkJoinPool pool = new ForkJoinPool();
		int[] array = new int[10000];
		Task5 task = new Task5(array, 0, array.length);
		pool.execute(task);
		while(!task.isDone()){
			showLog(pool);
			TimeUnit.SECONDS.sleep(1);
		}
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.DAYS);
		showLog(pool);
		System.out.println("Main： End.");
	}
	
	private static void showLog(ForkJoinPool pool){
		System.out.println("*************************");
		System.out.println("Fork/Join Pool log");
		//为线程池的建立的期望的并发级别
		System.out.println("Fork/Join Pool: Parallelism: "+pool.getParallelism());
		//线程池中工作者线程的数量
		System.out.println("Fork/Join Pool: Pool Size: "+pool.getPoolSize());
		//正在执行任务的线程数
		System.out.println("Fork/Join Pool: Active Thread Count: "+pool.getActiveThreadCount());
		//当前正在工作且未在任何同步机制中被阻塞的线程数
		System.out.println("Fork/Join Pool: Running Thread Count: "+pool.getRunningThreadCount());
		//已经提交到线程池但未开始执行的任务数
		System.out.println("Fork/Join Pool: Queued Submission: "+pool.getQueuedSubmissionCount());
		//已经提交到线程池且开始执行的任务数
		System.out.println("Fork/Join Pool: Queued Task: "+pool.getQueuedTaskCount());
		//线程池中是否有未开始执行的等待任务
		System.out.println("Fork/Join Pool: Queued Submissions: "+pool.hasQueuedSubmissions());
		//一个工作者线程从另一个线程中偷得的任务的次数
		System.out.println("Fork/Join Pool: Steal Count: "+pool.getStealCount());
		//线程池是否已经完成执行
		System.out.println("Fork/Join Pool: Terminated: "+pool.isTerminated());
		System.out.println("*************************");
	}

}

class Task5 extends RecursiveAction {

	private static final long serialVersionUID = 1L;
	private int[] array;
	private int start, end;

	public Task5(int[] array, int start, int end) {
		super();
		this.array = array;
		this.start = start;
		this.end = end;
	}

	@Override
	protected void compute() {
		if((end-start)>100){
			int mid = (start+end)/2;
			Task5 task1 = new Task5(array, start, mid);
			Task5 task2 = new Task5(array, mid, end);
			
			task1.fork();
			task2.fork();
			
			task1.join();
			task2.join();
		}else{
			for(int i=start;i<end;i++){
				array[i]++;
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
