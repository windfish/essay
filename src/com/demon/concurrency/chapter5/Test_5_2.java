package com.demon.concurrency.chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * 创建 Fork/Join 线程池
 * @author fish
 * @version 2016年7月29日 上午11:16:59
 */
public class Test_5_2 {

	/**
	 * 更新产品价格的任务，最初的任务负责更新任务列表中所有的元素
	 * 使用10作为参考大小（ReferenceSize），如果一个任务需要更新大于10个元素，它会将这个列表分为两部分，然后创建两个任务来更新
	 */
	public static void main(String[] args) {
		ProductListGenerator generator = new ProductListGenerator();
		List<Product> products = generator.generate(10000);
		Task2 task = new Task2(products, 0, products.size(), 2.00);
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		forkJoinPool.execute(task);
		
		do{
			System.out.println("Main: Thread count: "+forkJoinPool.getActiveThreadCount());
			System.out.println("Main: Thread steal: "+forkJoinPool.getStealCount());
			System.out.println("Main: Parallelism: "+forkJoinPool.getParallelism());
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(!task.isDone());
		
		forkJoinPool.shutdown();
		if(task.isCompletedNormally()){
			System.out.println("Main: The task has completed normally.");
		}
		
		for(int i=0;i<products.size();i++){
			Product p = products.get(i);
			if(p.getPrice() != 12){
				System.out.println("Product "+p.getName()+": "+p.getPrice());
			}
		}
		System.out.println("Main: End of the program.");
	}

}

class Product {
	private String name;
	private double price;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
}

class ProductListGenerator {
	public List<Product> generate(int size) {
		List<Product> list = new ArrayList<Product>();
		for(int i=0;i<size;i++){
			Product product = new Product();
			product.setName("Product #"+i);
			product.setPrice(10);
			list.add(product);
		}
		return list;
	}
}

class Task2 extends RecursiveAction {

	private static final long serialVersionUID = 1L;
	
	private List<Product> products;
	//决定任务执行时对产品的分块
	private int first;
	private int last;
	private double increment;

	public Task2(List<Product> products, int first, int last, double increment) {
		super();
		this.products = products;
		this.first = first;
		this.last = last;
		this.increment = increment;
	}

	@Override
	protected void compute() {
		/*
		 * 如果任务大于10个，将拆分这些元素为两部分，创建两个任务，并将拆分的部分相应地分配给新创建的任务。
		 */
		if(last-first<10){
			updatePrices();
		}else{
			int middle = (last+first)/2;
			System.out.println("Task: Pending tasks: "+getQueuedTaskCount());
			Task2 t1 = new Task2(products, first, middle+1, increment);
			Task2 t2 = new Task2(products, middle+1, last, increment);
			invokeAll(t1, t2);
		}
	}
	
	private void updatePrices(){
		for(int i=first;i<last;i++){
			Product p = products.get(i);
			p.setPrice(p.getPrice() + increment);
		}
	}
	
}
