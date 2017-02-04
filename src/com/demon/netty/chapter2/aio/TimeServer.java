package com.demon.netty.chapter2.aio;

/**
 * 服务端
 * @author xuliang
 * @since 2017年2月4日 下午5:47:35
 *
 */
public class TimeServer {

	public static void main(String[] args) {
		int port = 8080;
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
		new Thread(timeServer, "AIO-TimeServer-001").start();
	}
	
}
