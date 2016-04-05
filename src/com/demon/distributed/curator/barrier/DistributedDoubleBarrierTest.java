package com.demon.distributed.curator.barrier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * 双栅栏
 */
public class DistributedDoubleBarrierTest {
	private static final int QTY = 5;
	private static final String PATH = "/examples/barrier";

	public static void main(String[] args) throws Exception {
		try(TestingServer server = new TestingServer()){
			CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.start();
			
			ExecutorService service = Executors.newFixedThreadPool(QTY);
			for(int i=0;i<QTY;i++){
				final DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH, QTY);
				final int index = i;
				Callable<Void> task = new Callable<Void>() {
					
					@Override
					public Void call() throws Exception {
						TimeUnit.SECONDS.sleep((long) (3 * Math.random()));
						System.out.println("Client #"+index+" enter");
						barrier.enter();
						System.out.println("Client #"+index+" begins");
						TimeUnit.SECONDS.sleep((long) (3 * Math.random()));
						barrier.leave();
						System.out.println("Client #"+index+" leave");
						return null;
					}
				};
				service.submit(task);
			}
			
			service.shutdown();
			service.awaitTermination(10, TimeUnit.SECONDS);
		}
	}

}
