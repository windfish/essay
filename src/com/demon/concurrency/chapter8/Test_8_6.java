package com.demon.concurrency.chapter8;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * <pre>
 * 输出高效的日志信息
 * 
 * 日志系统是将信息输出到一个或多个目标上的一种机制。
 * 一个日志器有下面几个组件：
 * 	◆ 一个或多个处理器(Handler)：处理器决定目标和日志消息的格式。可把日志消息输出到控制台上、写到文件里或保存到数据库中
 * 	◆ 一个名称(Name)：一般来说，类中的日志记录器的名称是基于它的包名和类名的
 * 	◆ 一个级别(Level)：日志消息有一个关联的级别来表示它的重要性。日志记录器也有一个级别用来决定它输出什么级别的消息。日志记录器仅输出与它的级别相同重要或更重要的消息
 * </pre>
 * @author fish
 * @version 2016年8月24日 下午4:16:09
 */
public class Test_8_6 {

	public static void main(String[] args) {
		Logger logger = MyLogger.getLogger("Core");
		logger.entering("Core", "main()", args);
		Thread[] threads = new Thread[5];
		for(int i=0;i<threads.length;i++){
			logger.log(Level.INFO, "Launching thread: "+i);
			Task6 task = new Task6();
			threads[i] = new Thread(task);
			logger.log(Level.INFO, "Created thread: "+threads[i].getName());
			threads[i].start();
		}
		logger.log(Level.INFO, "All thread Created. Waiting for its finalization.");
		for(int i=0;i<threads.length;i++){
			try {
				threads[i].join();
				logger.log(Level.INFO, "Thread has finish its execution",threads[i]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.exiting("Core", "main()");
	}

}

class MyFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append("["+record.getLevel()+"] - "); //获取日志级别
		sb.append(new Date(record.getMillis())+" : "); //获取日志时间
		sb.append(record.getSourceClassName()+"."+record.getSourceMethodName()+" : "); //获取类名和方法名
		sb.append(record.getMessage()+"\n"); //获取日志消息
		return sb.toString();
	}
	
}

class MyLogger {
	private static Handler handler;
	
	public static Logger getLogger(String name){
		Logger logger = Logger.getLogger(name);
		logger.setLevel(Level.ALL); //设置日志级别
		try {
			if(handler == null){
				handler = new FileHandler("src/com/demon/concurrency/chapter8/recipe8_6.log");
				Formatter formatter = new MyFormatter();
				handler.setFormatter(formatter);
			}
			if(logger.getHandlers().length == 0){
				logger.addHandler(handler);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logger;
	}
	
}

class Task6 implements Runnable {

	@Override
	public void run() {
		Logger logger = MyLogger.getLogger(this.getClass().getName());
		logger.entering(Thread.currentThread().getName(), "run()"); //输出FINER 级别的消息，表示方法开始执行
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.exiting(Thread.currentThread().getName(), "run()"); //输出FINER 级别的消息，表示方法结束执行
	}
	
}
