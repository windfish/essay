package com.demon.distributed.curator.lock;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

/**
 * 读写锁，首先请求了一个写锁， 然后降级成读锁。 执行业务处理，然后释放读写锁。
 */
public class ClientReadWriteLocks {
	private final InterProcessReadWriteLock lock;
	private final InterProcessMutex readLock;
	private final InterProcessMutex writeLock;
	private final FakeLimitedResource resource;
	private final String clientName;
	
	public ClientReadWriteLocks(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
		this.resource = resource;
		this.clientName = clientName;
		this.lock = new InterProcessReadWriteLock(client, lockPath);
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
	}
	
	public void doWork(long time, TimeUnit unit) throws Exception {
		if(!writeLock.acquire(time, unit)){
			throw new IllegalStateException(clientName + " could not acquire the writeLock.");
		}
		System.out.println(clientName + " has the writeLock.");
		
		if (!readLock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the readLock");
        }
        System.out.println(clientName + " has the readLock too");
        
        try{
        	resource.use();
        }finally{
        	System.out.println(clientName + " releasing the lock");
        	readLock.release();
        	writeLock.release();
        }
	}
	
}
