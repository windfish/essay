package com.demon.distributed.curator.barrier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * 栅栏  Barrier
 * 
 * <pre>
 * 创建controlBarrier来设置栅栏和移除栅栏。 
 * 再创建了5个线程，在此Barrier上等待。 最后移除栅栏后所有的线程才继续执行。
 * </pre>
 */
public class DistributedBarrierTest {
	private static final int QTY = 5;
	private static final String PATH = "/examples/barrier";

	public static void main(String[] args) throws Exception {
		try(TestingServer server = new TestingServer()){
			CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.start();
			
			ExecutorService service = Executors.newFixedThreadPool(QTY);
			final DistributedBarrier controlBarrier = new DistributedBarrier(client, PATH);
			controlBarrier.setBarrier();
			
			for(int i=0;i<QTY;i++){
				final int index = i;
				Callable<Void> task = new Callable<Void>() {
					
					@Override
					public Void call() throws Exception {
						TimeUnit.SECONDS.sleep((long) (3 * Math.random()));
						System.out.println("Client #"+index+" wait on barrier");
						controlBarrier.waitOnBarrier();
						System.out.println("Client #"+index+" begins");
						return null;
					}
				};
				service.submit(task);
			}
			
			TimeUnit.SECONDS.sleep(10);
			System.out.println("all Barrier instances should wait the condition");
			
			controlBarrier.removeBarrier();
			
			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
		}
	}

}
