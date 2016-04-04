package com.demon.distributed.curator;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

public class LeaderElectionListener extends LeaderSelectorListenerAdapter {
	private final String name;
	private final LeaderSelector leaderSelector;
	private final AtomicInteger leaderCount = new AtomicInteger();
	
	public LeaderElectionListener(String name, LeaderSelector leaderSelector) {
		super();
		this.name = name;
		this.leaderSelector = leaderSelector;
	}
	
	public void start() {
		leaderSelector.start();
	}
	
	public void close(){
		leaderSelector.close();
	}

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		final int waitSeconds = (int) (Math.random()+1);
		System.out.println();
	}

}
