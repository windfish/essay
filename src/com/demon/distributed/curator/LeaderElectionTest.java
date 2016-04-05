package com.demon.distributed.curator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import com.google.common.collect.Lists;

/**
 * 与LeaderLatch相比， 通过LeaderSelectorListener可以对领导权进行控制， 
 * 在适当的时候释放领导权，这样每个节点都有可能获得领导权。 
 * 而LeaderLatch一根筋到死， 除非调用close方法，否则它不会释放领导权
 */
public class LeaderElectionTest {
	private static final int CLIENT_QTY = 10;
	private static final String PATH = "/examples/leader";
	
	public static void main(String[] args) throws Exception {
		List<CuratorFramework> clients = Lists.newArrayList();
		List<LeaderElectionListener> listeners = Lists.newArrayList();
		TestingServer server = new TestingServer();
		try {
			for(int i=0;i<CLIENT_QTY;i++){
				CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
				clients.add(client);
				LeaderElectionListener listener = new LeaderElectionListener(client, PATH, "Client #"+i);
				listeners.add(listener);
				client.start();
				listener.start();
			}
			
			System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
		} finally {
			System.out.println("shutting down...");
			for(LeaderElectionListener listener: listeners){
				CloseableUtils.closeQuietly(listener);
			}
			for(CuratorFramework client: clients){
				CloseableUtils.closeQuietly(client);
			}
			CloseableUtils.closeQuietly(server);
		}
	}

}
