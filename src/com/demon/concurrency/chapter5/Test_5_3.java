package com.demon.concurrency.chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.RecursiveTask;

/**
 * 合并任务的结果
 * @author fish
 * @version 2016年7月29日 下午4:53:38
 */
public class Test_5_3 {

	/*
	 * 在文档中查找一个词，实现两种任务：
	 * 一个文档任务，遍历全文查找这个词
	 * 一个行任务，在每一行中查找这个词
	 */
	public static void main(String[] args) {
		
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

class DocumentTask extends RecursiveTask<Integer> {
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
		int result;
		if(end-start < 10){
			result = processLines(document, start, end, word);
		}else{
			int mid = (start+end)/2;
			DocumentTask t1 = new DocumentTask(document, start, mid+1, word);
			DocumentTask t2 = new DocumentTask(document, mid+1, end, word);
			invokeAll(t1, t2);
			
			result = groupResults(t1.get(), t2.get());
		}
		return result;
	}
	
	private Integer processLines(String[][] document, int start, int end, String word){
		List<LineTask> tasks = new ArrayList<LineTask>();
		for(int i=start;i<end;i++){
			LineTask task = new LineTask(document[i], 0, document[i].length, word);
			tasks.add(task);
		}
	}
	
}
