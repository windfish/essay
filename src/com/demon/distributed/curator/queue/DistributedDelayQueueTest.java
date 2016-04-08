package com.demon.distributed.curator.queue;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedDelayQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

/**
 * <pre>
 * DistributedDelayQueue  延时消费队列
 * </pre>
 */
public class DistributedDelayQueueTest {
	private static final String PATH = "/path/queue";

	public static void main(String[] args) throws Exception {
		TestingServer server = new TestingServer();
		CuratorFramework client = null;
		DistributedDelayQueue<String> queue = null;
		try {
			client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.getCuratorListenable().addListener(new CuratorListener() {
				@Override
				public void eventReceived(CuratorFramework client,
						CuratorEvent event) throws Exception {
					System.out.println("CuratorEvent: "+event.getType().name());
				}
			});
			client.start();
			
			QueueConsumer<String> consumer = createQueueConsumer();
			QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), PATH);
			queue = builder.buildDelayQueue();
			queue.start();
			
			for(int i=0;i<10;i++){
				queue.put("test msg #"+i, System.currentTimeMillis()+10000);
				System.out.println("msg #"+i+" time is: "+System.currentTimeMillis());
				TimeUnit.SECONDS.sleep((long) (3 * Math.random()));
			}
			
			TimeUnit.SECONDS.sleep(15);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtils.closeQuietly(queue);
			CloseableUtils.closeQuietly(client);
			CloseableUtils.closeQuietly(server);
		}
	}
	
	private static QueueSerializer<String> createQueueSerializer(){
		return new QueueSerializer<String>() {
			@Override
			public byte[] serialize(String str) {
				return str.getBytes();
			}
			@Override
			public String deserialize(byte[] bytes) {
				return new String(bytes);
			}
		};
	}
	
	private static QueueConsumer<String> createQueueConsumer(){
		return new QueueConsumer<String>() {
			@Override
			public void stateChanged(CuratorFramework client,
					ConnectionState state) {
				System.out.println("connection new state: "+state.name());
			}
			@Override
			public void consumeMessage(String message) throws Exception {
				System.out.println("consume one message: " + message + ", time is: "+System.currentTimeMillis()); 
			}
		};
	}

}
