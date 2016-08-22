package com.demon.concurrency.chapter7;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * 定制运行在Fork/Join 框架中的任务
 * @author fish
 * @version 2016年8月22日 上午11:11:27
 */
public class Test_7_8 {

	public static void main(String[] args) {
		int array[] = new int[10000];
		ForkJoinPool pool = new ForkJoinPool();
		Task8 task = new Task8("Task", array, 0, array.length);
		pool.invoke(task);
		pool.shutdown();
		for(int i=1000;i<1010;i++){
			System.out.println("Main: "+i+": "+array[i]);
		}
		System.out.println("Main: End.");
	}

}

abstract class MyWorkerTask extends ForkJoinTask<Void> {

	private static final long serialVersionUID = 1L;
	private String name;

	public MyWorkerTask(String name) {
		super();
		this.name = name;
	}

	/**
	 * ForkJoinTask 类的抽象方法，当任务不返回任何结果时，这个方法必须返回null
	 */
	@Override
	public Void getRawResult() {
		return null;
	}

	/**
	 * ForkJoinTask 类的抽象方法，当任务不返回任何结果时，设置方法体为空
	 */
	@Override
	protected void setRawResult(Void value) {
		
	}

	/**
	 * 类的主方法
	 */
	@Override
	protected boolean exec() {
		Date startDate = new Date();
		compute();
		Date finishDate = new Date();
		long diff = finishDate.getTime() - startDate.getTime();
		System.out.printf("MyWorkerTask: %s: %d Milliseconds to compute.\n",name, diff);
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * 任务主逻辑，由子类实现
	 */
	protected abstract void compute();
	
}

class Task8 extends MyWorkerTask {

	private static final long serialVersionUID = 1L;
	private int array[];
	private int start, end;

	public Task8(String name, int array[], int start, int end) {
		super(name);
		this.array = array;
		this.start = start;
		this.end = end;
	}

	/**
	 * 由开始和结束属性来决定的数组的元素块。
	 * 如果元素块中超过100个元素，则拆分这个块为两部分，并创建两个Task 对象来处理各部分。
	 * 使用 invokeAll() 方法传递这些任务到pool
	 */
	@Override
	protected void compute() {
		if((end-start)>100){
			int mid = (end+start)/2;
			Task8 task1 = new Task8(this.getName()+"1", array, start, mid);
			Task8 task2 = new Task8(this.getName()+"2", array, mid, end);
			invokeAll(task1,task2);
		}else{
			for(int i=start;i<end;i++){
				array[i]++;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
