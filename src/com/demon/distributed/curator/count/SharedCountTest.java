package com.demon.distributed.curator.count;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import com.google.common.collect.Lists;

/**
 * <pre>
 * SharedCount 使用int计数的计数器
 * 
 * 使用baseCount来监听计数值(addListener方法)。 任意的SharedCount， 只要使用相同的path，都可以得到这个计数值
 * 使用5个线程为计数值增加一个10以内的随机数
 * </pre>
 */
public class SharedCountTest implements SharedCountListener {
	private static final int QTY = 5;
	private static final String PATH = "/explames/counter";

	public static void main(String[] args) throws Exception {
		final Random random = new Random();
		SharedCountTest test = new SharedCountTest();
		try(TestingServer server = new TestingServer()){
			CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
			client.start();
			
			SharedCount baseCount = new SharedCount(client, PATH, 0);
			baseCount.addListener(test);
			baseCount.start();
			
			List<SharedCount> lists = Lists.newArrayList();
			ExecutorService service = Executors.newFixedThreadPool(QTY);
			for(int i=0;i<QTY;i++){
				final SharedCount count = new SharedCount(client, PATH, 0);
				lists.add(count);
				Callable<Void> task = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						count.start();
						TimeUnit.SECONDS.sleep(random.nextInt(10));
						//trySetCount 设置计数器，第一个参数提供当前的VersionedValue,如果期间其它client更新了此计数值， 你的更新可能不成功
						//但是这时你的client更新了最新的值，所以失败了你可以尝试再更新一次。 而setCount是强制更新计数器的值
						System.out.println("Increment: "+count.trySetCount(count.getVersionedValue(), count.getCount()+random.nextInt(10)));
						return null;
					}
				};
				service.submit(task);
			}
			
			service.shutdown();
			service.awaitTermination(10, TimeUnit.SECONDS);
			
			 for (int i = 0; i < QTY; ++i) {
                lists.get(i).close();
            }
            baseCount.close();
		}
	}

	@Override
	public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
		System.out.println("State Changed: "+arg1.toString());
	}

	@Override
	public void countHasChanged(SharedCountReader arg0, int arg1) throws Exception {
		System.out.println("Counter's value is changed to "+arg1);
	}

}
