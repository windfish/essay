package com.demon.concurrency.chapter3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch 的使用
 * 会议系统等待与会人员的进入
 */
public class Test_3_4 {

	public static void main(String[] args) {
		Videoconference conference = new Videoconference(10);
		Thread cThread = new Thread(conference);
		cThread.start();
		
		for(int i=0;i<10;i++){
			Participant participant = new Participant(conference, "participant #"+i);
			Thread p = new Thread(participant);
			p.start();
		}
	}

}

/**
 * 视频会议模拟类
 */
class Videoconference implements Runnable {
	private CountDownLatch controller;
	
	public Videoconference(int number) {
		controller = new CountDownLatch(number);
	}
	
	/**
	 * 参与会议的人进入会议
	 */
	public void arrive(String name){
		System.out.println(name+" has arrived.");
		controller.countDown();
		System.out.println("Videoconference: Waiting for "+controller.getCount()+" participants.");
	}

	@Override
	public void run() {
		System.out.println("Videoonference: Initialization: "+controller.getCount()+" participants.");
		try {
			controller.await();
			System.out.println("Videoconference: All participants have come.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

class Participant implements Runnable {
	private Videoconference conference;
	private String name;

	public Participant(Videoconference conference, String name) {
		super();
		this.conference = conference;
		this.name = name;
	}

	@Override
	public void run() {
		long duration = (long) (Math.random()*10);
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conference.arrive(name);
	}
	
}
