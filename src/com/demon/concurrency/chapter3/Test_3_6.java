package com.demon.concurrency.chapter3;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Phaser
 * 同步三个并发任务，这三个任务在三个不同的文件夹及其子文件夹下查找过去24小时内修改过的扩展名是.log的文件。
 * 分为三个步骤：
 * 	1、在指定的文件夹下获得扩展名是.log的文件；
 * 	2、对第一步的结果进行过滤，删除修改时间超过24小时的文件；
 * 	3、打印结果到控制台。
 * 在第一步和第二步结束时，都会检查是否存在文件，不存在文件的话，对应的线程将结束执行并从phaser 中删除
 */
public class Test_3_6 {

	public static void main(String[] args) {
		//指定参与同步的线程是3个
		Phaser phaser = new Phaser(3);
		
		FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
		FileSearch app = new FileSearch("C:\\Program Files", "log", phaser);
		FileSearch documents = new FileSearch("C:\\Users\\edwin\\Documents", "log", phaser);
		
		Thread systemT = new Thread(system);
		systemT.start();
		Thread appT = new Thread(app);
		appT.start();
		Thread documentsT = new Thread(documents);
		documentsT.start();
		
		try {
			systemT.join();
			appT.join();
			documentsT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Terminated: "+phaser.isTerminated());
	}

}

class FileSearch implements Runnable {
	//查找的文件夹
	private String initPath;
	//查找的文件扩展名
	private String suffix;
	//查找到的文件的完整路径
	private List<String> results;
	private Phaser phaser;

	public FileSearch(String initPath, String suffix, Phaser phaser) {
		super();
		this.initPath = initPath;
		this.suffix = suffix;
		this.phaser = phaser;
		results = new ArrayList<String>();
	}
	
	/**
	 * 处理文件和文件夹
	 */
	private void directorProcess(File file){
		File list[] = file.listFiles();
		if(list != null){
			for(int i=0;i<list.length;i++){
				if(list[i].isDirectory()){
					directorProcess(list[i]);
				}else{
					fileProcess(list[i]);
				}
			}
		}
	}
	
	/**
	 * 处理文件
	 */
	private void fileProcess(File file){
		if(file.getName().endsWith(suffix)){
			results.add(file.getAbsolutePath());
		}
	}
	
	/**
	 * 删除修改时间超过24小时的文件
	 */
	private void filterResults(){
		List<String> newResults = new ArrayList<String>();
		long actualDate = new Date().getTime();
		for(int i=0;i<results.size();i++){
			File file = new File(results.get(i));
			long fileDate = file.lastModified();
			if(actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)){
				newResults.add(results.get(i));
			}
		}
		results = newResults;
	}
	
	/**
	 * 检查结果集是不是空的，在第一阶段和第二阶段结束时调用
	 */
	private boolean checkResults(){
		if(results.isEmpty()){
			System.out.println(Thread.currentThread().getName()+": Phaser "+phaser.getPhase()+": 0 results.");
			System.out.println(Thread.currentThread().getName()+": Phaser "+phaser.getPhase()+": End.");
			
			//通知phaser 当前线程已完成当前阶段，并且不参与下一阶段的操作
			phaser.arriveAndDeregister();
			return false;
		}else{
			System.out.println(Thread.currentThread().getName()+": Phaser "+phaser.getPhase()+": "+results.size());
			
			//通知phaser 当前线程已完成当前阶段，需要阻塞直到其他线程完成当前阶段
			phaser.arriveAndAwaitAdvance();
			return true;
		}
	}
	
	/**
	 * 将结果集打印到控制台
	 */
	private void showInfo(){
		for(int i=0;i<results.size();i++){
			File file = new File(results.get(i));
			System.out.println(Thread.currentThread().getName()+": "+file.getAbsolutePath());
		}
		phaser.arriveAndAwaitAdvance();
	}

	@Override
	public void run() {
		//使查找工作在所有线程都被创建之后再开始
		phaser.arriveAndAwaitAdvance();
		System.out.println(Thread.currentThread().getName()+": Starting.");
		
		//第一阶段：查询文件夹及子文件夹下的文件
		File file = new File(initPath);
		if(file.isDirectory()){
			directorProcess(file);
		}
		if(!checkResults()){
			return;
		}
		System.out.println("----------------------------------------");
		//第二阶段：过滤24小时内的文件
		filterResults();
		if(!checkResults()){
			return;
		}
		System.out.println("----------------------------------------");
		//第三阶段：展示信息
		showInfo();
		phaser.arriveAndDeregister();
		System.out.println(Thread.currentThread().getName()+": Work completed.");
	}
	
}
