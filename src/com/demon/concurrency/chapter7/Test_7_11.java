package com.demon.concurrency.chapter7;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现原子对象
 * @author fish
 * @version 2016年8月24日 上午10:03:20
 */
public class Test_7_11 {

	public static void main(String[] args) {
		ParkingCounter counter = new ParkingCounter(5);
		Sensor1 sensor1 = new Sensor1(counter);
		Sensor2 sensor2 = new Sensor2(counter);
		
		Thread thread1 = new Thread(sensor1);
		Thread thread2 = new Thread(sensor2);
		thread1.start();
		thread2.start();
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Main: Number of cars: "+counter.get());
		System.out.println("Main: End.");
	}

}

class ParkingCounter extends AtomicInteger {
	private static final long serialVersionUID = 1L;
	private int maxNumber; //存储停车场里可停汽车的最大数量
	
	public ParkingCounter(int maxNumber) {
		set(0);
		this.maxNumber = maxNumber;
	}
	
	/**
	 * 使用get() 获取内部计数器的值，如果该值小于最大值，就增加车的数量；否则车的数量不能增加并返回false
	 */
	public boolean carIn(){
		while(true){
			int value = get();
			if(value == maxNumber){
				System.out.println("ParkingCounter: The parking lot is full.");
				return false;
			}else{
				int newValue = value+1;
				boolean changed = compareAndSet(value, newValue);
				if(changed){
					System.out.println("ParkingCounter: A car has entered.");
					return true;
				}
			}
		}
	}
	
	public boolean carOut(){
		while(true){
			int value = get();
			if(value == 0){
				System.out.println("ParkingCounter: The parking lot is empty.");
				return false;
			}else{
				int newValue = value-1;
				boolean changed = compareAndSet(value, newValue);
				if(changed){
					System.out.println("ParkingCounter: A car has gone out.");
					return true;
				}
			}
		}
	}
	
}

class Sensor1 implements Runnable {
	private ParkingCounter counter;

	public Sensor1(ParkingCounter counter) {
		super();
		this.counter = counter;
	}

	@Override
	public void run() {
		counter.carIn();
		counter.carIn();
		counter.carIn();
		counter.carIn();
		counter.carOut();
		counter.carOut();
		counter.carOut();
		counter.carIn();
		counter.carIn();
		counter.carIn();
	}
	
}

class Sensor2 implements Runnable {
	private ParkingCounter counter;

	public Sensor2(ParkingCounter counter) {
		super();
		this.counter = counter;
	}

	@Override
	public void run() {
		counter.carIn();
		counter.carOut();
		counter.carOut();
		counter.carOut();
		counter.carIn();
		counter.carIn();
		counter.carIn();
		counter.carIn();
		counter.carIn();
	}
	
}
