package com.demon.distributed.curator.node;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode.Mode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.KillSession;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

/**
 * 创建了两个节点，一个是临时节点，一个事持久化的节点。 client重连后临时节点不存在了。
 */
public class PersistentEphemeralNodeTest {
	private static final String PERSISTENT_PATH = "/examples/node";
	private static final String EPHEMERAL_PATH = "/examples/ephemeralNode";

	public static void main(String[] args) throws Exception {
		TestingServer server = new TestingServer();
		CuratorFramework client = null;
		PersistentEphemeralNode node = null;
		try {
			client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(100, 3));
			client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				@Override
				public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
					System.out.println("state changed: "+arg1.name());
				}
			});
			client.start();
			
			node = new PersistentEphemeralNode(client, Mode.EPHEMERAL, EPHEMERAL_PATH, "test".getBytes());
			node.start();
			node.waitForInitialCreate(10, TimeUnit.SECONDS);
			String actualPath = node.getActualPath();
			System.out.println("node "+actualPath+" value: "+new String(client.getData().forPath(EPHEMERAL_PATH)));
			
			client.create().forPath(PERSISTENT_PATH, "test persistent node".getBytes());
			System.out.println("node "+PERSISTENT_PATH+" value: "+client.getData().forPath(PERSISTENT_PATH));
			
			//切断session
			KillSession.kill(client.getZookeeperClient().getZooKeeper(), server.getConnectString());
			System.out.println("node " + actualPath + " doesn't exist: " + (client.checkExists().forPath(actualPath) == null));
            System.out.println("node " + PERSISTENT_PATH + " value: " + new String(client.getData().forPath(PERSISTENT_PATH)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			CloseableUtils.closeQuietly(node);
			CloseableUtils.closeQuietly(client);
			CloseableUtils.closeQuietly(server);
		}
	}

}
