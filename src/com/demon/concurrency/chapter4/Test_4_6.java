package com.demon.concurrency.chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 运行多个任务并处理所有结果
 * @author fish
 * @version 2016年7月21日 下午2:59:17
 */
public class Test_4_6 {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Task> list = new ArrayList<Task>();
		for(int i=0;i<3;i++){
			Task task = new Task("Task #"+i);
			list.add(task);
		}
		List<Future<Result>> resultList = null;
		try {
			resultList = executor.invokeAll(list);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		
		System.out.println("Main: Printing the results.");
		for(int i=0;i<resultList.size();i++){
			Future<Result> future = resultList.get(i);
			try {
				Result result = future.get();
				System.out.println(result.getName()+": "+result.getValue());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

}

class Result {
	private String name;
	private int value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}

class Task implements Callable<Result> {
	private String name;

	public Task(String name) {
		super();
		this.name = name;
	}

	@Override
	public Result call() throws Exception {
		System.out.println(name+": Starting.");
		try {
			long duration = (long) (Math.random()*10);
			System.out.println(name+": Waiting "+duration+" seconds for results.");
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		int value = 0;
		for(int i=0;i<5;i++){
			value += (int)(Math.random()*100);
		}
		Result result = new Result();
		result.setName(name);
		result.setValue(value);
		System.out.println(name+": End.");
		return result;
	}
	
}
