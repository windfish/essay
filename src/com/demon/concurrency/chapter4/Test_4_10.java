package com.demon.concurrency.chapter4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 执行器中控制任务的完成
 * FutureTask.done()
 * @author fish
 * @version 2016年7月22日 上午10:08:19
 */
public class Test_4_10 {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		ResultTask results[] = new ResultTask[5];
		for(int i=0;i<results.length;i++){
			ExecutableTask task = new ExecutableTask("Task "+i);
			results[i] = new ResultTask(task);
			executor.submit(results[i]);
		}
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//取消任务
		for(int i=0;i<results.length;i++){
			results[i].cancel(true);
		}
		
		for(int i=0;i<results.length;i++){
			if(!results[i].isCancelled()){
				try {
					System.out.println(results[i].get());
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		executor.shutdown();
		
	}

}

class ExecutableTask implements Callable<String> {
	private String name; //存储任务的名称

	public ExecutableTask(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String call() throws Exception {
		long duration = (long) (Math.random()*10);
		System.out.println(name+": Waiting "+duration+" seconds for results.");
		TimeUnit.SECONDS.sleep(duration);
		return "Hello world, I'm "+name;
	}
	
}

class ResultTask extends FutureTask<String> {
	private String name;

	public ResultTask(Callable<String> callable) {
		super(callable);
		this.name = ((ExecutableTask)callable).getName();
	}
	
	@Override
	protected void done() {
		if(isCancelled()){
			System.out.println(name+": canceled.");
		}else{
			System.out.println(name+": finished.");
		}
	}
	
}
