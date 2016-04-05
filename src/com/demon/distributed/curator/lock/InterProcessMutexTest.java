package com.demon.distributed.curator.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

/**
 * <pre>
 * 测试可重入锁
 * 
 * 
 * 生成10个client， 每个client重复执行10次 请求锁–访问资源–释放锁的过程。
 * 每个client都在独立的线程中。 结果可以看到，锁是随机的被每个实例排他性的使用。
 * 
 * 既然是可重用的，你可以在一个线程中多次调用acquire,在线程拥有锁时它总是返回true。
 * 
 * 不应该在多个线程中用同一个InterProcessMutex， 你可以在每个线程中都生成一个InterProcessMutex实例，它们的path都一样，这样它们可以共享同一个锁。
 * </pre>
 */
public class InterProcessMutexTest {
	private static final int QTY = 5;
	private static final int REPETITIONS = QTY * 5;
	private static final String Path = "/examples/locks";

	public static void main(String[] args) throws Exception {
		final FakeLimitedResource resource = new FakeLimitedResource();
		ExecutorService service = Executors.newFixedThreadPool(QTY);
		final TestingServer server = new TestingServer();
		try{
			for(int i=0;i<QTY;i++){
				final int index = i;
				Callable<Void> task = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
						try{
							client.start();
							final ClientInterProcessMutexLock clientLock = new ClientInterProcessMutexLock(client, Path, resource, "Client #"+index);
							for(int j=0;j<REPETITIONS;j++){
								clientLock.doWork(10, TimeUnit.SECONDS);
							}
						}catch(Throwable e){
							e.printStackTrace();
						}finally{
							CloseableUtils.closeQuietly(client);
						}
						
						return null;
					}
				};
				service.submit(task);
			}
			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
		}finally{
			CloseableUtils.closeQuietly(server);
		}
	}

}
