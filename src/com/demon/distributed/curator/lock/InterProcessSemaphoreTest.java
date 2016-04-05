package com.demon.distributed.curator.lock;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * <pre>
 * 信号量
 * 
 * 创建10个租约，先获得了5个租约，获取成功
 * 接着请求了一个租约，因为semaphore还有5个租约，所以请求可以满足，返回一个租约，还剩4个租约。 
 * 然后再请求5个租约，因为租约不够，阻塞到超时，还是没能满足，返回结果为null。
 * </pre>
 */
public class InterProcessSemaphoreTest {
	private static final int MAX_LEASE = 10;
	private static final String PATH = "/examples/locks";
	
	public static void main(String[] args) throws Exception {
		FakeLimitedResource resource = new FakeLimitedResource();
		try(TestingServer server = new TestingServer()){
			CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.start();
			
			InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, PATH, MAX_LEASE);
			Collection<Lease> leases = semaphore.acquire(5);
			System.out.println("get " + leases.size() + " leases");
			Lease lease = semaphore.acquire();
			System.out.println("get another lease");
			
			resource.use();
			
			Collection<Lease> leases2 = semaphore.acquire(5, 10, TimeUnit.SECONDS);
			System.out.println("timeout and acquire return " + leases2);
			
			System.out.println("return one lease");
			semaphore.returnLease(lease);
			
			System.out.println("return 5 lease");
			semaphore.returnAll(leases);
		}
	}

}
