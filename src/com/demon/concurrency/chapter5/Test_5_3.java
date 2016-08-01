package com.demon.concurrency.chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * 合并任务的结果
 * @author fish
 * @version 2016年7月29日 下午4:53:38
 */
public class Test_5_3 {

	/**
	 * 在文档中查找一个词，实现两种任务：
	 * 一个文档任务，遍历全文查找这个词
	 * 一个行任务，在每一行中查找这个词
	 */
	public static void main(String[] args) {
		DocumentMock mock = new DocumentMock();
		String[][] document = mock.generateDocument(100, 1000, "the");
		DocumentTask task = new DocumentTask(document, 0, 100, "the");
		
		ForkJoinPool pool = new ForkJoinPool();
		pool.execute(task);
		do{
			System.out.println("********************************");
			System.out.println("Main: Parallelism: "+pool.getParallelism());
			System.out.println("Main: Active Threads: "+pool.getActiveThreadCount());
			System.out.println("Main: Task Count: "+pool.getQueuedTaskCount());
			System.out.println("Main: Steal Count: "+pool.getStealCount());
			System.out.println("********************************");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(!task.isDone());
		
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("Main: The word appears "+task.get()+" in the document.");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}

/**
 * 生成字符串矩阵用来模拟一个文档
 * @author fish
 * @version 2016年7月29日 下午5:06:39
 */
class DocumentMock {
	private String words[] = {"the","hello","goodbye","packt","java","thread","pool","random","class","main"};
	
	/**
	 * 生成字符串矩阵
	 * @param numLines 行数
	 * @param numWords 每行单词数
	 * @param word 准备查找的词
	 */
	public String[][] generateDocument(int numLines, int numWords, String word){
		int counter = 0;
		String document[][] = new String[numLines][numWords];
		Random random = new Random();
		for(int i=0;i<numLines;i++){
			for(int j=0;j<numWords;j++){
				int index = random.nextInt(words.length);
				document[i][j] = words[index];
				if(document[i][j].equals(word)){
					counter++;
				}
			}
		}
		System.out.println("DocumentMock: The word appears "+counter+" times in the document.");
		return document;
	}
}

/**
 * 文档任务，这个类的任务处理 start 到 end 的文档行
 * 如果行数小于10，那么每行创建一个LineTask 对象处理
 * 如果行数大于10，那么将任务分为两组，并创建两个DocumentTask 来处理
 * @author fish
 * @version 2016年8月1日 上午11:59:58
 */
class DocumentTask extends RecursiveTask<Integer> {
	private static final long serialVersionUID = 1L;
	private String document[][];
	private int start,end;
	private String word;

	public DocumentTask(String[][] document, int start, int end, String word) {
		super();
		this.document = document;
		this.start = start;
		this.end = end;
		this.word = word;
	}

	@Override
	protected Integer compute() {
		Integer result = 0;
		if(end-start < 10){
			result = processLines(document, start, end, word);
		}else{
			int mid = (start+end)/2;
			DocumentTask t1 = new DocumentTask(document, start, mid+1, word);
			DocumentTask t2 = new DocumentTask(document, mid+1, end, word);
			invokeAll(t1, t2);
			
			try {
				result = groupResults(t1.get(), t2.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private Integer processLines(String[][] document, int start, int end, String word){
		List<LineTask> tasks = new ArrayList<LineTask>();
		for(int i=start;i<end;i++){
			LineTask task = new LineTask(document[i], 0, document[i].length, word);
			tasks.add(task);
		}
		invokeAll(tasks);
		int result = 0;
		for(int i=0;i<tasks.size();i++){
			LineTask task = tasks.get(i);
			try {
				result += task.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return Integer.valueOf(result);
	}
	
	private Integer groupResults(Integer number1, Integer number2){
		Integer result;
		result = number1 + number2;
		return result;
	}
	
}

/**
 * 行任务，这个类的任务处理文档中一行的一组词
 * 如果一组词的个数小于100，那么任务将直接搜索
 * 如果一组词的个数大于100，那么将任务拆分为两组，并创建两个LineTask 来处理
 * @author fish
 * @version 2016年8月1日 下午12:01:45
 */
class LineTask extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;
	private String line[];
	private int start, end;
	private String word;

	public LineTask(String[] line, int start, int end, String word) {
		super();
		this.line = line;
		this.start = start;
		this.end = end;
		this.word = word;
	}

	@Override
	protected Integer compute() {
		Integer result = null;
		if(end-start < 100){
			result = count(line, start, end, word);
		}else{
			int mid = (start+end)/2;
			LineTask task1 = new LineTask(line, start, mid+1, word);
			LineTask task2 = new LineTask(line, mid+1, end, word);
			invokeAll(task1, task2);
			try {
				result = groupResults(task1.get(), task2.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private Integer count(String line[], int start, int end, String word){
		int counter = 0;
		for(int i=start;i<end;i++){
			if(line[i].equals(word)){
				counter++;
			}
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return counter;
	}
	
	private Integer groupResults(Integer number1, Integer number2){
		Integer result;
		result = number1 + number2;
		return result;
	}
	
}
