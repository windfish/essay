package com.demon.distributed.lock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * 这里利用zookeeper的EPHEMERAL_SEQUENTIAL类型节点及watcher机制，来简单实现分布式锁。
 * 主要思想：
 * 1、开启10个线程，在disLocks节点下各自创建名为sub的EPHEMERAL_SEQUENTIAL节点；
 * 2、获取disLocks节点下所有子节点，排序，如果自己的节点编号最小，则获取锁；
 * 3、否则watch排在自己前面的节点，监听到其删除后，进入第2步（重新检测排序是防止监听的节点发生连接失效，导致的节点删除情况）；
 * 4、删除自身sub节点，释放连接；
 * 
 */
public class DistributedLock implements Watcher {
	private int threadId;
	private ZooKeeper zk = null;
	private String selfPath;
	private String waitPath;
	private String LOG_PREFIX_OF_THREAD;
	private static final int SESSION_TIMEOUT = 10000;
	private static final String GROUP_PATH = "/disLocks";
	private static final String SUB_PATH = "/disLocks/sub";
	private static final String CONNECTION_IP = "192.168.222.200:2181,192.168.222.200:2182,192.168.222.200:2183";
	private static final int THREAD_NUM = 10;
	//确保连接zk成功
	private CountDownLatch connectedSemaphore = new CountDownLatch(1);
	//确保所有线程运行结束
	private static CountDownLatch threadSemaphore = new CountDownLatch(THREAD_NUM);
	private static final Logger LOG = Logger.getLogger(DistributedLock.class);
	
	public DistributedLock(int id) {
		this.threadId = id;
		LOG_PREFIX_OF_THREAD = "【第"+threadId+"个线程】";
	}
	
	public static void main(String[] args) {
		LOG.info("=========>模拟获得锁运行开始");
		for(int i=0;i<THREAD_NUM;i++){
			final int threadId = i+1;
			new Thread(){
				public void run() {
					try {
						DistributedLock lock = new DistributedLock(threadId);
						lock.connect(CONNECTION_IP, SESSION_TIMEOUT);
						synchronized (threadSemaphore) {
							lock.createPath(GROUP_PATH, "该节点由线程"+threadId+"创建", true);
						}
						lock.getLock();
					} catch (Exception e) {
						LOG.error("【第"+threadId+"个线程】抛出异常");
						e.printStackTrace();
					}
				};
			}.start();
		}
		try {
			threadSemaphore.await();
			LOG.info("=========>所有线程运行结束");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取锁
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void getLock() throws KeeperException, InterruptedException {
		selfPath = zk.create(SUB_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		LOG.info(LOG_PREFIX_OF_THREAD + "创建锁的路径是：" + selfPath);
		if(checkIsMinPath()){
			getLockSuccess();
		}
	}
	
	/**
	 * 创建节点
	 * @param path 节点路径
	 * @param data 初始数据类型
	 * @param needWatch
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public boolean createPath(String path, String data, boolean needWatch) throws KeeperException, InterruptedException {
		if(zk.exists(path, needWatch) == null){
			String createPath = zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			LOG.info(LOG_PREFIX_OF_THREAD+"节点创建成功，path："+createPath+"，content："+data);
		}
		return true;
	}
	
	/**
	 * 创建zk连接
	 * @param ip zk服务器地址
	 * @param sessionTimeout Session超时时间
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void connect(String ip, int sessionTimeout) throws IOException, InterruptedException {
		zk = new ZooKeeper(ip, sessionTimeout, this);
		connectedSemaphore.await();
	}
	
	/**
	 * 成功获得锁
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void getLockSuccess() throws KeeperException, InterruptedException {
		if(zk.exists(this.selfPath, false) == null){
			LOG.info(LOG_PREFIX_OF_THREAD + "创建的节点已经不存在了");
			return;
		}
		LOG.info(LOG_PREFIX_OF_THREAD+"获取锁成功，开始干活");
		Thread.sleep(2000);
		LOG.info(LOG_PREFIX_OF_THREAD+"完成工作，删除节点："+this.selfPath);
		zk.delete(this.selfPath, -1);
		closeConnection();
		threadSemaphore.countDown();
	}
	
	/**
	 * 关闭zk连接
	 */
	public void closeConnection() {
		if(this.zk != null){
			try {
				this.zk.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LOG.info("关闭zk连接");
	}
	
	public boolean checkIsMinPath() throws KeeperException, InterruptedException {
		List<String> subNodes = zk.getChildren(GROUP_PATH, false);
		Collections.sort(subNodes);
		int index = subNodes.indexOf(selfPath.substring(GROUP_PATH.length()+1));
		switch (index) {
			case -1:{
				LOG.info(LOG_PREFIX_OF_THREAD+"节点不存在，path："+selfPath);
				return false;
			}
			case 0:{
				LOG.info(LOG_PREFIX_OF_THREAD+"是最小的节点，path："+selfPath);
				return true;
			}
			default:{
				this.waitPath = GROUP_PATH+"/"+subNodes.get(index-1);
				LOG.info(LOG_PREFIX_OF_THREAD+"获取子节点中，排在我前面的是"+this.waitPath);
				try {
					zk.getData(waitPath, true, new Stat());
					return false;
				}catch(KeeperException e){
					if(zk.exists(waitPath, false) == null){
						LOG.info(LOG_PREFIX_OF_THREAD+"子节点中，排在我前面的"+waitPath+"已失踪");
						return checkIsMinPath();
					}else {
						throw e;
					}
				}
			}
		}
	}

	@Override
	public void process(WatchedEvent event) {
		if(event == null){
			return;
		}
		Event.KeeperState keeperState = event.getState();
		Event.EventType eventType = event.getType();
		
		if(Event.KeeperState.SyncConnected == keeperState){
			if(Event.EventType.None == eventType){
				LOG.info(LOG_PREFIX_OF_THREAD+"成功连接上zk服务器");
				connectedSemaphore.countDown();
			}else if(Event.EventType.NodeDeleted == eventType && event.getPath().equals(waitPath)){
				LOG.info(LOG_PREFIX_OF_THREAD+"收到通知，排在我前面的节点已删除");
				try {
					if(checkIsMinPath()){
						getLockSuccess();
					}
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else if(Event.KeeperState.Disconnected == keeperState){
			LOG.info(LOG_PREFIX_OF_THREAD+"与zk服务器断开连接");
		}else if(Event.KeeperState.AuthFailed == keeperState){
			LOG.info(LOG_PREFIX_OF_THREAD+"权限检查失败");
		}else if(Event.KeeperState.Expired == keeperState){
			LOG.info(LOG_PREFIX_OF_THREAD+"会话失效");
		}
	}

}
