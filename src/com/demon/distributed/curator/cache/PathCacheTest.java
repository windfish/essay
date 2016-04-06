package com.demon.distributed.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

public class PathCacheTest {
	private static final String PATH = "/examples/cache";

	public static void main(String[] args) throws Exception {
		try(TestingServer server = new TestingServer()){
			CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(100, 3));
			client.start();
			
			PathChildrenCache pathCache = new PathChildrenCache(client, PATH, true);
			pathCache.start();
			
			client.create().creatingParentsIfNeeded().forPath(PATH+"/0001", "cache test 0001".getBytes());
			client.setData().forPath(PATH, "cache test".getBytes());
			
			for(ChildData data: pathCache.getCurrentData()){
				System.out.println(data.getPath() + " = " + new String(data.getData()));
			}
			
			client.delete().forPath(PATH+"/0001");
			client.delete().forPath(PATH);
			
			pathCache.close();
			client.close();
		}
	}

}
