package com.demon.netty.chapter2.nio;

/**
 * 服务端
 * @author xuliang
 * @since 2017年2月4日 下午4:04:33
 *
 */
public class TimeServer {

	public static void main(String[] args) {
		int port = 8080;
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-TimeServer-001").start();
	}
	
}
