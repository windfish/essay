package com.demon.jsvc.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAbstractCycleJob extends Thread {
	
	private Logger logger = LoggerFactory.getLogger(BaseAbstractCycleJob.class);
	
	/**
	 * 是否为停止
	 */
	public static boolean IS_THREAD_SHUTDOWN = false;
	
	protected boolean RUN_FLAG = true;	
	protected long SLEEP_TIME = 20000;
	
	public BaseAbstractCycleJob() {
		DaemonJob.register(getClass());
	}
	
	public BaseAbstractCycleJob(ThreadGroup group, String name) {
		super(group, name);
        DaemonJob.register(getClass());
    }
	
	protected boolean isShutdown() {
		return IS_THREAD_SHUTDOWN;
	}
	
	@Override
	public void run() {
		initSleepTime();
		while (RUN_FLAG) {
			if (isShutdown()) {
				stopTask();
				logger.info(" xxxxxxxx ------ " + this.getClass().getName() + " task stop ------ xxxxxxxx");
				break;
			}
			try {
				reportTaskStatus();
				doJob();
				Thread.sleep(SLEEP_TIME);
			} catch (Exception e) {
				logger.error(" xxxxxx --- do job exception:" + getClass().getName(), e);
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					logger.error("thread sleep exception:", e1);
					return ;
				}
			}
		}
	}
	
	/**
	 * 初始化没有任务的时候休眠多长时间
	 */
	public abstract void initSleepTime();
	
	/**
	 * 具体的业务逻辑处理
	 */
	public abstract void doJob() throws Exception;
	
	/**
	 * 上报任务的运行时间
	 */
	public void reportTaskStatus() {
		
	}
	
	/**
	 * 停止任务
	 */
	public void stopTask() {
		RUN_FLAG = false;
		DaemonJob.removeRegister(getClass());
	}
}
