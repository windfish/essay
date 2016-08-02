package com.demon.concurrency.chapter6;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 使用阻塞式线程安全列表 LinkedBlockingDeque
 * @author fish
 * @version 2016年8月2日 下午4:26:15
 */
public class Test_6_3 {

	public static void main(String[] args) {
		LinkedBlockingDeque<String> list = new LinkedBlockingDeque<String>(3); //指定列表容量为3
		Client client = new Client(list);
		Thread thread = new Thread(client);
		thread.start();
		for(int i=0;i<5;i++){
			for(int j=0;j<3;j++){
				try {
					String request = list.take();
					System.out.printf("Main: Request: %s at %s. Size: %d\n",request,new Date(),list.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Main: End of the program.");
	}

}

class Client implements Runnable {
	private LinkedBlockingDeque<String> requestList;

	public Client(LinkedBlockingDeque<String> requestList) {
		super();
		this.requestList = requestList;
	}

	@Override
	public void run() {
		for(int i=0;i<3;i++){
			for(int j=0;j<5;j++){
				StringBuilder request = new StringBuilder();
				request.append(i);
				request.append(":");
				request.append(j);
				try {
					requestList.put(request.toString());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.printf("Client: %s at %s.\n",request,new Date());
			}
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Client: End.");
	}
	
}
