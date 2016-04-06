package com.demon.distributed.curator;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

/**
 * 两种创建客户端的方式，以及API的使用方式
 */
public class CreateClientTest {
	private static final String PATH = "/examples/basic";

	public static void main(String[] args) throws Exception {
		TestingServer server = new TestingServer();
		CuratorFramework client = null;
		try {
			client = createSimple(server.getConnectString());
			client.start();
			client.create().creatingParentsIfNeeded().forPath(PATH, "test".getBytes());
			CloseableUtils.closeQuietly(client);
			
			client = createWithOptions(server.getConnectString(), new ExponentialBackoffRetry(1000, 3), 1000, 1000);
			client.start();
			System.out.println(PATH + " = " + new String(client.getData().forPath(PATH)));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtils.closeQuietly(client);
			CloseableUtils.closeQuietly(server);
		}
	}
	
	/**
	 * 使用CuratorFrameworkFactory工厂创建Curator客户端
	 */
	public static CuratorFramework createSimple(String connectionString){
		// these are reasonable arguments for the ExponentialBackoffRetry. 
        // The first retry will wait 1 second - the second will wait up to 2 seconds - the
        // third will wait up to 4 seconds.
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // The simplest way to get a CuratorFramework instance. This will use default values.
        // The only required arguments are the connection string and the retry policy
        return CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
	}
	
	/**
	 * 使用builder创建Curator客户端
	 */
	public static CuratorFramework createWithOptions(String connectionString, RetryPolicy policy, 
			int connectionTimeoutMs, int sessionTimeoutMs){
		return CuratorFrameworkFactory.builder().connectString(connectionString)
				.retryPolicy(policy)
				.connectionTimeoutMs(connectionTimeoutMs)
				.sessionTimeoutMs(sessionTimeoutMs)
				.build();
	}
	
	/*
	 * API使用方式
	 */
	public static void create(CuratorFramework client, String path, byte[] payload) throws Exception {
        //创建节点，并添加节点数据
        client.create().forPath(path, payload);
    }

    public static void createEphemeral(CuratorFramework client, String path, byte[] payload) throws Exception {
        //创建临时节点，并添加节点数据
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
    }

    public static String createEphemeralSequential(CuratorFramework client, String path, byte[] payload) throws Exception {
        //创建保护性的临时节点
        return client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, payload);
    }

    public static void setData(CuratorFramework client, String path, byte[] payload) throws Exception {
        //往节点写数据
        client.setData().forPath(path, payload);
    }

    public static void setDataAsync(CuratorFramework client, String path, byte[] payload) throws Exception {
        //事件监听，异步通知
        CuratorListener listener = new CuratorListener() {
			@Override
			public void eventReceived(CuratorFramework paramCuratorFramework,
					CuratorEvent paramCuratorEvent) throws Exception {
				
			}
        };
        client.getCuratorListenable().addListener(listener);
        //设置节点数据，并通过监听完成通知
        client.setData().inBackground().forPath(path, payload);
    }

    public static void setDataAsyncWithCallback(CuratorFramework client, BackgroundCallback callback, String path, byte[] payload) throws Exception {
        //另一个异步通知方式，通过BackgroundCallback实现
        client.setData().inBackground(callback).forPath(path, payload);
    }

    public static void delete(CuratorFramework client, String path) throws Exception {
        //删除节点
        client.delete().forPath(path);
    }

    public static void guaranteedDelete(CuratorFramework client, String path) throws Exception {
        //删除节点并保证它完成
        client.delete().guaranteed().forPath(path);
    }

    public static List<String> watchedGetChildren(CuratorFramework client, String path) throws Exception {
        //获得子节点并设置监听，异步通知
        return client.getChildren().watched().forPath(path);
    }

    public static List<String> watchedGetChildren(CuratorFramework client, String path, Watcher watcher) throws Exception {
        //获得子节点并设置指定的监听
        return client.getChildren().usingWatcher(watcher).forPath(path);
    }

}
