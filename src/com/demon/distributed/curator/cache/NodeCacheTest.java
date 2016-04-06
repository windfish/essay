package com.demon.distributed.curator.cache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.KeeperException;

/**
 * NodeCache  监控节点变化的缓存
 */
public class NodeCacheTest {
	private static final String PATH = "/examples/nodeCache";

	public static void main(String[] args) throws Exception {
		TestingServer server = new TestingServer();
		CuratorFramework client = null;
		NodeCache cache = null;
		try {
			client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.start();
			
			cache = new NodeCache(client, PATH);
			cache.start();
			
			processCommands(client, cache);
		} finally {
			CloseableUtils.closeQuietly(cache);
			CloseableUtils.closeQuietly(client);
			CloseableUtils.closeQuietly(server);
		}
	}
	
	/**
	 * 添加监控，监控节点状态的改变
	 */
	private static void addListener(final NodeCache cache){
		NodeCacheListener listener = new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				if(cache.getCurrentData() != null){
					System.out.println("Node Changed: "+cache.getCurrentData().getPath()+",value: "+new String(cache.getCurrentData().getData()));
				}
			}
		};
		cache.getListenable().addListener(listener);
	}
	
	private static void processCommands(CuratorFramework client, NodeCache cache){
		printHelp();
		try {
			addListener(cache);
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			boolean done = false;
			while(!done){
				System.out.print(">");
				String line = in.readLine();
				if(line == null){
					break;
				}
				String command = line.trim();
				//按空格区分命令和数据
				String[] parts = command.split("\\s");
				if(parts.length == 0){
					continue;
				}
				String operation = parts[0];
				String[] args = Arrays.copyOfRange(parts, 1, parts.length);
				if("help".equalsIgnoreCase(operation) || "?".equalsIgnoreCase(operation)){
					printHelp();
				}else if("q".equalsIgnoreCase(operation) || "quit".equalsIgnoreCase(operation)){
					done = true;
				}else if("set".equalsIgnoreCase(operation)){
					setValue(client, command, args);
				}else if("remove".equalsIgnoreCase(operation)){
					remove(client);
				}else if("show".equalsIgnoreCase(operation)){
					show(cache);
				}
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 展示节点缓存数据
	 */
	private static void show(NodeCache cache){
		if(cache.getCurrentData() != null){
			System.out.println(cache.getCurrentData().getPath()+" = "+new String(cache.getCurrentData().getData()));
		}else{
			System.out.println("cache has no value.");
		}
	}
	
	/**
	 * 删除节点
	 */
	private static void remove(CuratorFramework client) throws Exception{
		try {
			client.delete().forPath(PATH);
		} catch (KeeperException.NoNodeException e) {
		}
	}
	
	/**
	 * 新增、修改节点值
	 */
	private static void setValue(CuratorFramework client, String command, String[] args) throws Exception{
		if(args.length != 1){
			System.err.println("syntax error (expected set <value>): " + command);
			return;
		}
		byte[] bytes = args[0].getBytes();
		try {
			client.create().forPath(PATH, bytes);
		} catch (KeeperException.NoNodeException e) {
			client.create().creatingParentsIfNeeded().forPath(PATH, bytes);
		} catch (KeeperException.NodeExistsException e){
			client.setData().forPath(PATH, bytes);
		}
	}
	
	private static void printHelp() {
        System.out.println("An example of using PathChildrenCache. This example is driven by entering commands at the prompt:\n");
        System.out.println("set <value>: Adds or updates a node with the given name");
        System.out.println("remove: Deletes the node with the given name");
        System.out.println("show: Display the node's value in the cache");
        System.out.println("quit: Quit the example");
        System.out.println();
    }

}
