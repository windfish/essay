package com.demon.concurrency.chapter5;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * 异步运行任务
 * @author fish
 * @version 2016年8月1日 下午3:23:58
 */
public class Test_5_4 {

	/**
	 * 在一个文件夹及其子文件中来搜索带有指定扩展名的文件。
	 * 对于每一个子文件，任务将以异步的方式发送一个新的任务给 ForkJoinPool 类
	 */
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		
		FolderProcesser system = new FolderProcesser("c:\\Windows", "log");
		FolderProcesser apps = new FolderProcesser("c:\\Program Files", "log");
		FolderProcesser documents = new FolderProcesser("C:\\Users\\edwin\\Documents", "log");
		
		pool.execute(system);
		pool.execute(apps);
		pool.execute(documents);
		
		do{
			System.out.println("***********************************");
			System.out.println("Main: Parallelism: "+pool.getParallelism());
			System.out.println("Main: Active Threads: "+pool.getActiveThreadCount());
			System.out.println("Main: Task Count: "+pool.getQueuedTaskCount());
			System.out.println("Main: Steal Count: "+pool.getStealCount());
			System.out.println("***********************************");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(!system.isDone() || !apps.isDone() || !documents.isDone());
		
		pool.shutdown();
		List<String> results;
		results = system.join();
		System.out.println("System: "+results.size()+" files found.");
		results = apps.join();
		System.out.println("Apps: "+results.size()+" files found.");
		results = documents.join();
		System.out.println("Documents: "+results.size()+" files found.");
	}

}

class FolderProcesser extends RecursiveTask<List<String>> {

	private static final long serialVersionUID = 1L;
	//文件夹路径
	private String path;
	//文件扩展名
	private String extension;

	public FolderProcesser(String path, String extension) {
		super();
		this.path = path;
		this.extension = extension;
	}

	@Override
	protected List<String> compute() {
		List<String> list = new ArrayList<String>();
		List<FolderProcesser> tasks = new ArrayList<FolderProcesser>();
		
		File file = new File(path);
		File content[] = file.listFiles();
		
		if(content != null){
			for(int i=0;i<content.length;i++){
				if(content[i].isDirectory()){
					FolderProcesser task = new FolderProcesser(content[i].getAbsolutePath(), extension);
					task.fork();
					tasks.add(task);
				}else{
					if(checkFile(content[i].getName())){
						list.add(content[i].getAbsolutePath());
					}
				}
			}
			if(tasks.size() > 50){
				System.out.println(file.getAbsolutePath()+": "+tasks.size()+" tasks run.");
			}
			addResultsFromTasks(list, tasks);
		}
		
		return list;
	}
	
	/**
	 * 遍历任务列表中的任务，并调用 join()等待任务结束，并调用addAll()将任务返回的结果增加到结果列表中
	 * @param list 结果的字符串列表
	 * @param tasks 任务的列表
	 */
	private void addResultsFromTasks(List<String> list, List<FolderProcesser> tasks){
		for(FolderProcesser task: tasks){
			list.addAll(task.join());
		}
	}
	
	/**
	 * 检查文件名是否以扩展名结尾
	 */
	private boolean checkFile(String name){
		return name.endsWith(extension);
	}
	
}
