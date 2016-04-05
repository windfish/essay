package com.demon.distributed.curator;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

public class LeaderElectionListener extends LeaderSelectorListenerAdapter implements Closeable {
	private final String name;
	private final LeaderSelector leaderSelector;
	/**记录此client获得领导权的次数*/
	private final AtomicInteger leaderCount = new AtomicInteger();
	
	public LeaderElectionListener(CuratorFramework client, String path, String name) {
		this.name = name;
		leaderSelector = new LeaderSelector(client, path, this);
		//leaderSelector.autoRequeue();保证在此实例释放领导权之后还可能获得领导权
		leaderSelector.autoRequeue();
	}
	
	public void start() {
		leaderSelector.start();
	}
	
	@Override
	public void close() throws IOException {
		leaderSelector.close();
	}

	/**
	 * 通过takeLeadership进行任务的分配，并且不要返回
	 * 如果你想要要此实例一直是leader的话可以加一个死循环。
	 */
	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		final int waitSeconds = (int) (5 * Math.random()) + 1;
		System.out.println(name + " is now the leader. Waiting " + waitSeconds + "seconds...");
		System.out.println(name + " has bean leader " + leaderCount.getAndIncrement() + " times before.");
		try {
			TimeUnit.SECONDS.sleep(waitSeconds);
		} catch (InterruptedException e) {
			System.out.println(name + " was interrupted.");
			Thread.currentThread().interrupt();
		}finally {
			System.out.println(name + " relinquishing leadership.");
		}
	}

}
