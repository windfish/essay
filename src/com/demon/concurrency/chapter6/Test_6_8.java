package com.demon.concurrency.chapter6;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用原子变量
 * @author fish
 * @version 2016年8月12日 下午3:25:31
 */
public class Test_6_8 {

	public static void main(String[] args) {
		Account account = new Account();
		account.setBalance(1000);
		
		Company company = new Company(account);
		Thread t1 = new Thread(company);
		
		Bank bank = new Bank(account);
		Thread t2 = new Thread(bank);
		
		System.out.println("Account: Initial Balance: "+account.getBalance());
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
			System.out.println("Account: Final Balance: "+account.getBalance());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

class Account {
	private AtomicLong balance;
	
	public Account() {
		balance = new AtomicLong();
	}

	public long getBalance() {
		return balance.get();
	}

	public void setBalance(long balance) {
		this.balance.set(balance);
	}
	
	public void addAmount(long amount){
		this.balance.getAndAdd(amount);
	}
	
	public void substractAmount(long amount){
		this.balance.getAndAdd(-amount);
	}
	
}

class Company implements Runnable {
	private Account account;

	public Company(Account account) {
		super();
		this.account = account;
	}

	@Override
	public void run() {
		for(int i=0;i<10;i++){
			account.addAmount(1000);
		}
	}
	
}

class Bank implements Runnable {
	private Account account;

	public Bank(Account account) {
		super();
		this.account = account;
	}

	@Override
	public void run() {
		for(int i=0;i<10;i++){
			account.substractAmount(1000);
		}
	}
	
}
