package com.demon.concurrency.chapter3;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier
 * 在数字矩阵中查找一个数字（使用分治编程技术）：
 *   这个矩阵会被分为几个子集，然后每个线程在一个子集中查找，当所有的线程都完成查找，最终的任务将统一这些结果。
 */
public class Test_3_5 {

	public static void main(String[] args) {
		final int ROWS = 10000; //矩阵的行数
		final int NUMBERS = 1000; //矩阵的列数
		final int SEARCH = 5; //查找的数字
		final int PARTICIPANTS = 5; //查找线程个数
		final int LINES_PARTICIPANTS = 2000; //每个查找线程处理的行数
		
		MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);
		Results results = new Results(ROWS);
		Grouper grouper = new Grouper(results);
		
		//等待查找线程结束，运行计算结果的线程
		CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);
		
		Searcher searcher[] = new Searcher[PARTICIPANTS];
		for(int i=0;i<PARTICIPANTS;i++){
			searcher[i] = new Searcher(i*LINES_PARTICIPANTS, (i+1)*LINES_PARTICIPANTS, mock, results, SEARCH, barrier);
			Thread t = new Thread(searcher[i]);
			t.start();
		}
		System.out.println("Main: The main thread has finished.");
	}

}

/**
 * 模拟矩阵类
 * 生成一个1~10的随机矩阵
 */
class MatrixMock {
	private int data[][];
	
	/**
	 * @param size   矩阵的行数
	 * @param length 矩阵的列数
	 * @param number 要查找的数字
	 */
	public MatrixMock(int size, int length, int number) {
		int counter = 0;
		data = new int[size][length];
		Random random = new Random();
		for(int i=0;i<size;i++){
			for(int j=0;j<length;j++){
				data[i][j] = random.nextInt(10);
				if(data[i][j] == number){
					counter++; //矩阵中有几个数字与需要查找
				}
			}
		}
		System.out.println("Mock: There are "+counter+" occurrences of number in matrix.");
	}
	
	/**
	 * 查找行数据
	 * @param row 矩阵行序号
	 */
	public int[] getRow(int row){
		if(row>=0 && row<data.length){
			return data[row];
		}
		return null;
	}
	
}

class Results {
	private int data[];
	
	public Results(int size) {
		data = new int[size];
	}
	
	public void setData(int position, int value){
		data[position] = value;
	}
	
	public int[] getData(){
		return data;
	}
}

/**
 * 查找线程
 */
class Searcher implements Runnable {
	//觉得查找的子集范围
	private int firstRow;
	private int lastRow;
	
	private MatrixMock mock;
	private Results results;
	//存放要查找的数字
	private int number;
	
	private CyclicBarrier barrier;
	
	/**
	 * @param firstRow 起始行数
	 * @param lastRow  终止行数
	 * @param mock     矩阵
	 * @param results  结果集
	 * @param number   查找的数字
	 * @param barrier  CyclicBarrier
	 */
	public Searcher(int firstRow, int lastRow, MatrixMock mock,
			Results results, int number, CyclicBarrier barrier) {
		super();
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.mock = mock;
		this.results = results;
		this.number = number;
		this.barrier = barrier;
	}

	@Override
	public void run() {
		//存放每行查找到数字的次数
		int counter;
		System.out.println(Thread.currentThread().getName()+": Processing line from "+firstRow+" to "+lastRow+".");
		for(int i=firstRow;i<lastRow;i++){
			int row[] = mock.getRow(i);
			counter = 0;
			for(int j=0;j<row.length;j++){
				if(row[j] == number){
					counter++;
				}
			}
			results.setData(i, counter);
		}
		System.out.println(Thread.currentThread().getName()+": Line processed.");
		try {
			barrier.await(); //等待其他线程执行结束
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}

/**
 * 计算矩阵中查找到的总次数，计算基于Results 对象
 */
class Grouper implements Runnable {
	private Results results;

	public Grouper(Results results) {
		super();
		this.results = results;
	}

	@Override
	public void run() {
		int finalResult = 0;
		System.out.println("Grouper: Processing result...");
		int data[] = results.getData();
		for(int number : data){
			finalResult += number;
		}
		System.out.println("Grouper: Total results: "+finalResult);
	}
	
}
