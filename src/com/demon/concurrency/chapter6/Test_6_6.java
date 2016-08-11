package com.demon.concurrency.chapter6;

import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 使用线程安全可遍历映射 
 * @author fish
 * @version 2016年8月11日 下午4:21:08
 */
public class Test_6_6 {

	/*
	 * 实现对联系人对象的映射
	 */
	public static void main(String[] args) {
		ConcurrentSkipListMap<String, Contact> map = new ConcurrentSkipListMap<String, Contact>();
		Thread threads[] = new Thread[25];
		int counter = 0;
		for(char c='A';c<'Z';c++){
			Task6 task = new Task6(map, String.valueOf(c));
			threads[counter] = new Thread(task);
			threads[counter].start();
			counter++;
		}
		for(int i=0;i<threads.length;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Main: Size of the map: "+map.size());
		Map.Entry<String, Contact> element;
		Contact contact;
		
		element = map.firstEntry();
		contact = element.getValue();
		System.out.printf("Main: First Entry: %s: %s\n",contact.getName(),contact.getPhone());
		
		element = map.lastEntry();
		contact = element.getValue();
		System.out.printf("Main: Last Entry: %s: %s\n",contact.getName(),contact.getPhone());
		
		System.out.println("Main: Submap from A1996 to B1002:");
		ConcurrentNavigableMap<String, Contact> submap = map.subMap("A1996", "B1002");
		do{
			element = submap.pollFirstEntry();
			if(element!=null){
				contact = element.getValue();
				System.out.println(contact.getName()+": "+contact.getPhone());
			}
		}while(element!=null);
	}

}

class Contact {
	private String name;
	private String phone;
	
	public Contact(String name, String phone) {
		super();
		this.name = name;
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}
	
}

class Task6 implements Runnable {
	private ConcurrentSkipListMap<String, Contact> map;
	private String id;

	public Task6(ConcurrentSkipListMap<String, Contact> map, String id) {
		super();
		this.map = map;
		this.id = id;
	}

	@Override
	public void run() {
		for(int i=0;i<1000;i++){
			Contact contact = new Contact(id, String.valueOf(i+1000));
			map.put(id+contact.getPhone(), contact);
		}
	}
	
}
