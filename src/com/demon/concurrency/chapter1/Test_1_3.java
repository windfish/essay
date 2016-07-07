package com.demon.concurrency.chapter1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;

/**
 * 指定线程的优先级，并输出线程的状态信息
 */
public class Test_1_3 implements Runnable {

	private int number;
	
	public Test_1_3(int number) {
		this.number = number;
	}
	
	@Override
	public void run() {
		for(int i=1;i<=10;i++){
			System.out.printf("%s : %d * %d = %d\n",Thread.currentThread().getName(),number,i,i*number);
		}
	}
	
	public static void main(String[] args) {
		Thread threads[] = new Thread[10];
		Thread.State status[] = new Thread.State[10];
		
		for(int i=0;i<10;i++){
			threads[i] = new Thread(new Test_1_3(i));
			if(i%2 == 0){
				threads[i].setPriority(Thread.MAX_PRIORITY);
			}else{
				threads[i].setPriority(Thread.MIN_PRIORITY);
			}
			threads[i].setName("Thread"+i);
		}
		
		try {
			FileWriter file = new FileWriter("src/com/demon/concurrency/chapter1/test_1_3_log.txt");
			PrintWriter pw = new PrintWriter(file);
			
			//写入线程初始状态
			for(int i=0;i<10;i++){
				pw.println("Main : Status of Thread"+i+" : "+threads[i].getState());
				pw.flush();
				status[i] = threads[i].getState();
			}
			
			//执行线程
			for(int i=0;i<10;i++){
				threads[i].start();
			}
			
			boolean finish = false;
			while(!finish){
				for(int i=0;i<10;i++){
					if(threads[i].getState() != status[i]){
						writeThreadInfo(pw, threads[i], status[i]);
						status[i] = threads[i].getState();
					}
				}
				finish = true;
				for(int i=0;i<10;i++){
					finish = finish && (threads[i].getState() == State.TERMINATED);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeThreadInfo(PrintWriter pw, Thread thread, State state){
		pw.printf("Main : Id %d - %s\n",thread.getId(),thread.getName());
		pw.printf("Main : Priority: %d\n",thread.getPriority());
		pw.printf("Main : Old Status: %s\n",state);
		pw.printf("Main : New Status: %s\n",thread.getState());
		pw.printf("Main : *************************\n");
		pw.flush();
	}

}
