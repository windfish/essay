package com.demon.concurrency.chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程执行器返回运行结果 Callable、Future
 * @author fish
 * @version 2016年7月20日 下午3:34:01
 */
public class Test_4_4 {

	public static void main(String[] args) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
		List<Future<Integer>> list = new ArrayList<Future<Integer>>();
		Random random = new Random();
		for(int i=0;i<10;i++){
			Integer number = random.nextInt(10);
			FactorialCalculator calculator = new FactorialCalculator(number);
			Future<Integer> result = executor.submit(calculator);
			list.add(result);
		}
		do{
			System.out.println("Main: Number of Completed Tasks: "+executor.getCompletedTaskCount());
			for(int i=0;i<list.size();i++){
				Future<Integer> result = list.get(i);
				System.out.println("Main: Task "+i+": "+result.isDone());
			}
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(executor.getCompletedTaskCount()<list.size()); //完成数量小于10，就一直重复
		
		System.out.println("Main: Results");
		for(int i=0;i<list.size();i++){
			Future<Integer> result = list.get(i);
			Integer number = null;
			try {
				number = result.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println("Main: Task "+i+": "+number);
		}
		
		executor.shutdown();
	}

}

class FactorialCalculator implements Callable<Integer> {
	private Integer number;

	public FactorialCalculator(Integer number) {
		super();
		this.number = number;
	}
	
	/**
	 * 返回number的阶乘
	 */
	@Override
	public Integer call() throws Exception {
		int result = 1;
		if(number==0 || number==1){
			result = 1;
		}else{
			for(int i=2;i<=number;i++){
				result *= i;
				TimeUnit.MILLISECONDS.sleep(20);
			}
		}
		System.out.println(Thread.currentThread().getName()+": "+result);
		return result;
	}
	
}
