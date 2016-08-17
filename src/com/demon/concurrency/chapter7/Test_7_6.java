package com.demon.concurrency.chapter7;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定制运行在定时线程池中的任务
 * @author fish
 * @version 2016年8月17日 上午9:51:02
 */
public class Test_7_6 {

	public static void main(String[] args) throws InterruptedException {
		MyScheduledThreadPoolExecutor executor = new MyScheduledThreadPoolExecutor(2);
		Task task = new Task();
		System.out.println("Main: "+new Date());
		executor.schedule(task, 1, TimeUnit.SECONDS); //延迟1秒执行任务
		TimeUnit.SECONDS.sleep(4);
		
		task = new Task();
		System.out.println("Main: "+new Date());
		executor.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS); //延迟1秒执行，然后每3秒执行一次
		TimeUnit.SECONDS.sleep(10);
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);
		System.out.println("Main: End.");
	}

}

/**
 * 定制任务类，能够在ScheduledThreadPoolExecutor 中执行。
 * 在定时线程池中执行的任务必须实现 RunnableScheduledFuture 接口。
 * 继承 FutureTask 类是因为该类提供了RunnableScheduledFuture 接口中声明的方法的有效实现。
 * @author fish
 * @version 2016年8月17日 上午11:13:05
 * @param <V> 泛型化参数是任务返回的数据类型
 */
class MyScheduledTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
	private RunnableScheduledFuture<V> task; //用来创建MyScheduledTask 任务对象
	private ScheduledThreadPoolExecutor executor; //用来执行MyScheduledTask 任务
	private long period;
	private long startDate;
	
	public MyScheduledTask(Runnable runnable, V result, 
			RunnableScheduledFuture<V> task, ScheduledThreadPoolExecutor executor) {
		super(runnable, result);
		this.task = task;
		this.executor = executor;
	}

	/**
	 * 如果是周期性任务且startDate 不等于0，则计算startDate 属性和当前时间的时间差作为返回值。
	 * 否则返回存放在task 属性中的延迟值。
	 */
	@Override
	public long getDelay(TimeUnit unit) {
		if(!isPeriodic()){
			return task.getDelay(unit);
		}else{
			if(startDate == 0){
				return task.getDelay(unit);
			}else{
				Date now = new Date();
				long delay = startDate - now.getTime();
				return unit.convert(delay, TimeUnit.MILLISECONDS);
			}
		}
	}

	@Override
	public int compareTo(Delayed o) {
		return task.compareTo(o);
	}

	@Override
	public boolean isPeriodic() {
		return task.isPeriodic();
	}
	
	@Override
	public void run() {
		if(isPeriodic() && !executor.isShutdown()){
			Date now = new Date();
			startDate = now.getTime()+period; //设定下次任务的执行时间
			executor.getQueue().add(this); //增加任务到线程池的任务列表中，周期性任务必须添加到线程池的任务列表中才能再次执行
		}
		System.out.println("Pre-MyScheduledTask: "+new Date());
		System.out.println("Pre-MyScheduledTask: isPeriodic: "+isPeriodic());
		super.runAndReset();
		System.out.println("Post-MyScheduledTask: "+new Date());
	}

	public void setPeriod(long period) {
		this.period = period;
	}
	
}

class MyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

	public MyScheduledThreadPoolExecutor(int corePoolSize) {
		super(corePoolSize);
	}
	
	/**
	 * 必须覆盖decorateTask 方法。
	 * 构造MyScheduledTask 任务对象并返回
	 */
	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable,
			RunnableScheduledFuture<V> task) {
		MyScheduledTask<V> myTask = new MyScheduledTask<V>(runnable, null, task, this);
		return myTask;
	}
	
	/**
	 * 调用父类方法并将返回值转换为MyScheduledTask 对象，并使用setPeriod 设置任务的周期
	 */
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
			long initialDelay, long period, TimeUnit unit) {
		ScheduledFuture<?> task = super.scheduleAtFixedRate(command, initialDelay, period, unit);
		MyScheduledTask<?> myTask = (MyScheduledTask<?>) task;
		myTask.setPeriod(TimeUnit.MILLISECONDS.convert(period, unit));
		return task;
	}
	
}

class Task implements Runnable {

	@Override
	public void run() {
		System.out.println("Task: Begin.");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Task: End.");
	}
	
}
