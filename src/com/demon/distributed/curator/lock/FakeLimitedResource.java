package com.demon.distributed.curator.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 共享资源，这个资源期望只能单线程的访问，否则会有并发问题
 */
public class FakeLimitedResource {
	private final AtomicBoolean isUse = new AtomicBoolean(false);
	
	public void use() throws InterruptedException {
		/*
		 * 真实环境中，会在这里访问或维护一个共享的资源
		 * 例子中在有锁的情况下，不会出现并发异常IllegalStateException
		 * 但在无锁的情况下，由于sleep了一段时间，很容易抛出异常
		 */
		//如果当前值等于false，则将当前值置为true，并返回true；如果当前值不等于false，则直接返回false
		if(!isUse.compareAndSet(false, true)){
			throw new IllegalStateException("Needs to be used by one client at a time");
		}
		try {
			TimeUnit.SECONDS.sleep((long) (3 * Math.random()));
		} finally {
			isUse.set(false);
		}
	}
}
