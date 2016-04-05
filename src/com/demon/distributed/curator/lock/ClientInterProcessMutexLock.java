package com.demon.distributed.curator.lock;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * 可重入锁，负责请求锁、使用资源、释放锁这样一个完整的访问过程
 */
public class ClientInterProcessMutexLock {
	private final InterProcessMutex lock;
	private final FakeLimitedResource resource;
	private final String clientName;
	
	public ClientInterProcessMutexLock(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
		this.resource = resource;
		this.clientName = clientName;
		lock = new InterProcessMutex(client, lockPath);
	}
	
	/**
	 * 访问资源的流程
	 * 获得锁--访问资源--释放锁
	 */
	public void doWork(long time, TimeUnit unit) throws Exception {
		if(!lock.acquire(time, unit)){
			throw new IllegalStateException(clientName + " could not acquire the lock.");
		}
		//测试锁的可重入性
		if(!lock.acquire(time, unit)){
			throw new IllegalStateException(clientName + " could not acquire the lock.");
		}
		try{
			System.out.println(clientName + " has the lock.");
			resource.use();
		}finally{
			System.out.println(clientName + " releasing the lock.");
			lock.release();
			lock.release();
		}
	}
	
}
