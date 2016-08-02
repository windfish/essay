package com.demon.concurrency.chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * 取消任务
 * @author fish
 * @version 2016年8月2日 上午9:56:35
 */
public class Test_5_6 {

	public static void main(String[] args) {
		ArrayGenerator generator = new ArrayGenerator();
		int array[] = generator.generatorArray(1000);
		TaskManager manager = new TaskManager();
		ForkJoinPool pool = new ForkJoinPool();
		SearchNumberTask task = new SearchNumberTask(array, 0, 1000, 5, manager);
		pool.execute(task);
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main: The program has finished.");
	}

}

class ArrayGenerator {
	public int[] generatorArray(int size) {
		int array[] = new int[size];
		Random random = new Random();
		for(int i=0;i<size;i++){
			array[i] = random.nextInt(10);
		}
		return array;
	}
}

/**
 * 存储在 ForkJoinPool 中执行的任务，并提供取消任务的方法
 * @author fish
 * @version 2016年8月2日 上午10:05:43
 */
class TaskManager {
	private List<ForkJoinTask<Integer>> tasks;
	
	public TaskManager() {
		tasks = new ArrayList<ForkJoinTask<Integer>>();
	}
	
	public void addTask(ForkJoinTask<Integer> task){
		tasks.add(task);
	}
	
	/**
	 * 根据传入的任务，取消其他的任务
	 */
	public void cancelTasks(ForkJoinTask<Integer> cancelTask){
		for(ForkJoinTask<Integer> task: tasks){
			if(task != cancelTask){
				task.cancel(true);
				((SearchNumberTask)task).writeCancelMessage();
			}
		}
	}
}

/**
 * 寻找整数数组中的数字
 * @author fish
 * @version 2016年8月2日 上午10:13:41
 */
class SearchNumberTask extends RecursiveTask<Integer> {
	private static final long serialVersionUID = 1L;
	private int numbers[];
	private int start, end;
	private int number;
	private TaskManager manager;
	private static final int NOT_FOUND = -1;
	
	public SearchNumberTask(int[] numbers, int start, int end, int number,
			TaskManager manager) {
		super();
		this.numbers = numbers;
		this.start = start;
		this.end = end;
		this.number = number;
		this.manager = manager;
	}

	@Override
	protected Integer compute() {
		System.out.println("Task: "+start+":"+end);
		int ret = 0;
		if(end - start > 10){
			ret = launchTasks();
		}else{
			ret = lookForNumber();
		}
		return ret;
	}
	
	/**
	 * 查找所要处理的元素，如果找到，就用TaskManager 取消所有的任务
	 * @return 元素所在位置；未找到返回-1
	 */
	private int lookForNumber(){
		for(int i=start;i<end;i++){
			if(numbers[i]==number){
				System.out.println("Task: Number "+number+" found in position "+i);
				manager.cancelTasks(this);
				return i;
			}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return NOT_FOUND;
	}
	
	/**
	 * 将要处理的元素分为两部分，创建两个task进行处理
	 * @return
	 */
	private int launchTasks(){
		int mid = (start+end)/2;
		SearchNumberTask task1 = new SearchNumberTask(numbers, start, mid, number, manager);
		SearchNumberTask task2 = new SearchNumberTask(numbers, mid, end, number, manager);
		manager.addTask(task1);
		manager.addTask(task2);
		//采用异步方法执行这两个任务
		task1.fork();
		task2.fork();
		
		int ret = task1.join();
		if(ret != -1){
			return ret;
		}
		ret = task2.join();
		return ret;
	}
	
	public void writeCancelMessage(){
		System.out.println("Task: Cancelled task from "+start+" to "+end);
	}
	
}
