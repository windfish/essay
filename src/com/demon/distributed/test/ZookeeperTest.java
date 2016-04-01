package com.demon.distributed.test;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Zookeeper Java API
 *
 */
public class ZookeeperTest {

	//会话超时时间
	public static final int SESSION_TIMEOUT = 30000;
	
	private Watcher watcher = new Watcher() {
		
		@Override
		public void process(WatchedEvent event) {
			System.out.println("watch process : " + event.getType());
		}
	};
	
	private ZooKeeper zooKeeper;
	
	/**
	 * 连接zookeeper
	 * @throws IOException
	 */
	@Before
	public void connect() throws IOException {
		zooKeeper = new ZooKeeper("192.168.222.200:2181,192.168.222.200:2182,192.168.222.200:2183", SESSION_TIMEOUT, watcher);
	}
	
	/**
	 * 关闭连接
	 */
	public void close() {
		try {
			zooKeeper.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>
	 * 创建一个znode
	 * CreateMode取值
	 * PERSISTENT  持久化节点
	 * PERSISTENT_SEQUENTIAL  顺序自动编号持久化节点，这种节点会根据当前已存在的节点数自动加1
	 * EPHEMERAL  临时节点，客户端session超时，这类节点就会自动删除
	 * EPHEMERAL_SEQUENTIAL  临时自动编号节点
	 * </pre>
	 */
	@Test
	public void testCreateZnode() {
		String result = null;
		try {
			result = zooKeeper.create("/zk001", "zk001data".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			//result = zooKeeper.create("/zk002", "zk001data".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("create znode result is : " + result);
	}
	
	/**
	 * 查看节点是否存在
	 */
	@Test
	public void testExists(){
		Stat stat = null;
		try {
			stat = zooKeeper.exists("/zk001", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(stat);
		System.out.println("stat version : " + stat.getVersion());
	}
	
	/**
	 * 获取节点数据
	 */
	@Test
	public void testGetData(){
		String result = null;
		try {
			byte[] bytes = zooKeeper.getData("/zk001", null, null);
			result = new String(bytes);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("get data is : " + result);
	}
	
	/**
	 * 获取数据，设置watch事件
	 */
	@Test
	public void testGetDataWatch(){
		String result = null;
		try {
			byte[] bytes = zooKeeper.getData("/zk001", new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					System.out.println("testGetDataWatch watch : " + event.getType());
				}
			}, null);
			result = new String(bytes);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("get node data is : "+result);
		
		//修改节点数据，触发watch，事件类型为NodeDataChange
		try {
			zooKeeper.setData("/zk001", "testGetDataWatch data".getBytes(), -1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//再次修改节点数据，不会触发watch，因为watch是一次性的，只会触发一次
		try {
			zooKeeper.setData("/zk001", "testGetDataWatch second data".getBytes(), -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 删除节点
	 */
	@Test
	public void testDelete(){
		try {
			zooKeeper.delete("/zk001", -1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	/**
	 * 获取指定节点下的子节点
	 */
	@Test
	public void testGetChild(){
		try {
			zooKeeper.create("/zk/001", "001".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zooKeeper.create("/zk/002", "002".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
			List<String> list = zooKeeper.getChildren("/zk", false);
			for(int i=0;i<list.size();i++){
				System.out.println("node {"+(i+1)+"} " + list.get(i));
			}
		} catch (Exception e) {
			Assert.fail();
		}
	}
	
}
