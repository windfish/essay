package com.demon.concurrency.chapter4;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 执行器中分离任务的启动和结果的处理
 * 使用 CompletionService 类，解决以下情形：
 * 	在一个对象中发送任务给执行器，在另一个对象里处理结果
 * CompletionService 类的submit()提交任务，poll()获取任务的结果
 * @author fish
 * @version 2016年7月26日 上午10:58:37
 */
public class Test_4_11 {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<String> service = new ExecutorCompletionService<String>(executor);
		
		ReportRequest faceRequest = new ReportRequest("face", service);
		ReportRequest onlineRequest = new ReportRequest("online", service);
		Thread faceThread = new Thread(faceRequest);
		Thread onlineThread = new Thread(onlineRequest);
		
		ReportProcessor processor = new ReportProcessor(service);
		Thread senderThread = new Thread(processor);
		
		System.out.println("Main: Starting the threads.");
		faceThread.start();
		onlineThread.start();
		senderThread.start();
		
		System.out.println("Main: Waiting the report generators.");
		try {
			faceThread.join();
			onlineThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Main: shutdown the executor.");
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		processor.setEnd(true);
		System.out.println("Main： Ends.");
	}

}

class ReportGenerator implements Callable<String> {
	private String sender;
	private String title;

	public ReportGenerator(String sender, String title) {
		super();
		this.sender = sender;
		this.title = title;
	}

	@Override
	public String call() throws Exception {
		try{
			long duration = (long) (Math.random()*10);
			System.out.println(sender+" send report in "+duration+" seconds.");
			TimeUnit.SECONDS.sleep(duration);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		String ret = sender+": "+title;
		return ret;
	}
	
}

/**
 * 模拟获取报告的请求类
 * @author fish
 * @version 2016年7月26日 上午11:18:02
 */
class ReportRequest implements Runnable {
	private String name;
	private CompletionService<String> service;

	public ReportRequest(String name, CompletionService<String> service) {
		super();
		this.name = name;
		this.service = service;
	}

	@Override
	public void run() {
		ReportGenerator generator = new ReportGenerator(name, "Report");
		service.submit(generator); //提交任务
	}
	
}

/**
 * 获取任务的结果
 * @author fish
 * @version 2016年7月26日 上午11:19:05
 */
class ReportProcessor implements Runnable {
	private CompletionService<String> service;
	private boolean end;

	public ReportProcessor(CompletionService<String> service) {
		super();
		this.service = service;
		this.end = false;
	}

	@Override
	public void run() {
		while(!end){
			try {
				Future<String> result = service.poll(20,TimeUnit.SECONDS); //获取执行完的任务的结果，若没有，等待20s
				if(result != null){
					String report = result.get();
					System.out.println("ReportReceiver: Report Received: "+report);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println("ReportReceiver: End.");
		}
	}

	public void setEnd(boolean end) {
		this.end = end;
	}
	
}
