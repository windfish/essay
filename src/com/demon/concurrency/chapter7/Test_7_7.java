package com.demon.concurrency.chapter7;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * 通过 ThreadFactory 接口为 Fork/Join 框架生成定制线程
 * @author fish
 * @version 2016年8月19日 上午11:51:59
 */
public class Test_7_7 {

	public static void main(String[] args) {
		MyWorkerThreadFactory factory = new MyWorkerThreadFactory();
		ForkJoinPool pool = new ForkJoinPool(4, factory, null, false);
		int array[] = new int[100000];
		for(int i=0;i<array.length;i++){
			array[i] = i;
		}
		MyRecursizeTask task = new MyRecursizeTask(array, 0, array.length);
		pool.execute(task);
		task.join();
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("Main: Result: "+task.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Main: End.");
	}

}

/**
 * 继承 ForkJoinWorkerThread 类，可以在Fork/Join 框架中使用
 * @author fish
 * @version 2016年8月22日 上午11:05:55
 */
class MyWorkerThread extends ForkJoinWorkerThread {
	//计数器，统计一个工作线程执行了多少任务
	private ThreadLocal<Integer> taskCounter = new ThreadLocal<Integer>();
	
	protected MyWorkerThread(ForkJoinPool pool) {
		super(pool);
	}
	
	/**
	 * 工作线程开始执行时，会被自动调用。
	 * 初始化计数器
	 */
	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("MyWorkerThread "+getId()+": Initializing task counter.");
		taskCounter.set(0);
	}
	
	/**
	 * 工作线程完成执行时，会被自动调用。
	 * 输出计数器的值到控制台
	 */
	@Override
	protected void onTermination(Throwable exception) {
		System.out.println("MyWorkerThread "+getId()+": "+taskCounter.get());
		super.onTermination(exception);
	}
	
	/**
	 * 用来增加计数器的值
	 */
	public void addTask(){
		int counter = taskCounter.get().intValue();
		counter++;
		taskCounter.set(counter);
	}
	
}

/**
 * 实现 ForkJoinWorkerThreadFactory 接口，可以在ForkJoinPool 中使用自定义的线程工厂
 * @author fish
 * @version 2016年8月22日 上午11:09:05
 */
class MyWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

	/**
	 * 创建一个新的线程对象
	 */
	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		return new MyWorkerThread(pool);
	}
	
}

class MyRecursizeTask extends RecursiveTask<Integer> {
	private static final long serialVersionUID = 1L;
	private int array[];
	private int start,end;

	public MyRecursizeTask(int[] array, int start, int end) {
		super();
		this.array = array;
		this.start = start;
		this.end = end;
	}
	
	@Override
	protected Integer compute() {
		int ret = 0;
		for(int i=start;i<end;i++){
			ret += array[i];
		}
		MyWorkerThread thread = (MyWorkerThread) Thread.currentThread();
		thread.addTask();
		return ret;
	}
	
	public Integer addResults(MyRecursizeTask task1, MyRecursizeTask task2){
		int value;
		try {
			value = task1.get().intValue()+task2.get().intValue();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			value=0;
		}
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return value;
	}
	
}
