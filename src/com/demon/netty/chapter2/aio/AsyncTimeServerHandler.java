package com.demon.netty.chapter2.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author xuliang
 * @since 2017年2月4日 下午5:48:38
 *
 */
public class AsyncTimeServerHandler implements Runnable {

	private int port;
	CountDownLatch latch;
	AsynchronousServerSocketChannel asyncSocketChannel;
	
	public AsyncTimeServerHandler(int port) {
		this.port = port;
		try {
			asyncSocketChannel = AsynchronousServerSocketChannel.open();
			asyncSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("The time server is start in port : "+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		/**
		 * CountDownLatch 的作用是完成一组正在执行的操作之前，允许当前线程一直阻塞，防止服务器执行完退出，仅demo使用
		 */
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void doAccept(){
		// 接收客户端的连接，由于是异步操作，传递一个CompletionHandler实例来接收accept操作成功的通知消息
		asyncSocketChannel.accept(this, new AcceptCompletionHandler());
	}

}
