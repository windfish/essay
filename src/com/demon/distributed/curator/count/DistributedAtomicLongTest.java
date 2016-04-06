package com.demon.distributed.curator.count;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;

import com.google.common.collect.Lists;

/**
 * <pre>
 * DistributedAtomicLong  使用long计数的计数器
 * 
 * 使用5个线程对计数器进行加一操作，如果成功，将操作前后的值打印出来
 * </pre>
 */
public class DistributedAtomicLongTest {
	private static final int QTY = 5;
    private static final String PATH = "/examples/counter";

	public static void main(String[] args) throws Exception {
		try (TestingServer server = new TestingServer()) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();
            
            List<DistributedAtomicLong> lists = Lists.newArrayList();
            ExecutorService service = Executors.newFixedThreadPool(QTY);
            for(int i=0;i<QTY;i++){
            	final DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));
            	lists.add(count);
            	
            	Callable<Void> task = new Callable<Void>() {
            		@Override
            		public Void call() throws Exception {
            			AtomicValue<Long> value = count.increment();
            			System.out.println("succeed :"+value.succeeded());
            			if(value.succeeded()){
            				System.out.println("Increment: from "+value.preValue()+" to "+value.postValue());
            			}
            			return null;
            		}
				};
				service.submit(task);
            }
            
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
		}
	}

}
