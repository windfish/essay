package com.demon.concurrency.chapter2;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 使用 Synchronized 实现同步方法
 * 用 Synchronized 声明的方法都是临界区，在Java 中，同一个对象的临界区，在同一时间只有一个允许被访问
 * 用 Synchronized 声明的静态方法，同时只能够被一个执行线程访问，但是其他线程可以访问该对象的非静态方法
 * </pre>
 */
public class Test_2_2 {
	public static void main(String[] args) {
		Account account = new Account();
		account.setBalance(1000);
		
		Company company = new Company(account);
		Thread t1 = new Thread(company);
		
		Bank bank = new Bank(account);
		Thread t2 = new Thread(bank);
		
		System.out.printf("Account: Initial Balance: %f\n",account.getBalance());
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
			System.out.printf("Account: Final Balance: %f\n",account.getBalance());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

class Account {
	private double balance;
	
	public synchronized void addAmount(double amount){
		double tmp = balance;
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tmp += amount;
		balance = tmp;
	}
	
	public synchronized void substractAmount(double amount){
		double tmp = balance;
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tmp -= amount;
		balance = tmp;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
}

class Bank implements Runnable {
	private Account account;
	
	public Bank(Account account) {
		this.account = account;
	}
	
	@Override
	public void run() {
		for(int i=0;i<100;i++){
			account.substractAmount(1000);
		}
	}
}

class Company implements Runnable {
	private Account account;
	
	public Company(Account account) {
		this.account = account;
	}
	
	@Override
	public void run() {
		for(int i=0;i<100;i++){
			account.addAmount(1000);
		}
	}
}
