package com.demon.distributed.curator.count;

import java.util.Random;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * SharedCount 使用int计数的计数器
 */
public class SharedCountTest implements SharedCountListener {
	private static final int QTY = 5;
	private static final String PATH = "/explames/counter";

	public static void main(String[] args) throws Exception {
		final Random random = new Random();
		SharedCountTest test = new SharedCountTest();
		try(TestingServer server = new TestingServer()){
			CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.start();
			
			SharedCount baseCount = new SharedCount(client, PATH, 0);
			baseCount.addListener(test);
			baseCount.start();
			
			
		}
	}

	@Override
	public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
		System.out.println("State Changed: "+arg1.toString());
	}

	@Override
	public void countHasChanged(SharedCountReader arg0, int arg1) throws Exception {
		System.out.println("Counter's value is changed to "+arg1);
	}

}
