package com.demon.netty.chapter2.nio;

/**
 * 客户端
 * @author xuliang
 * @since 2017年2月4日 下午4:59:11
 *
 */
public class TimeClient {

	public static void main(String[] args) {
		int port = 8080;
		new Thread(new TimeClientHandle("127.0.0.1", port), "NIO-TimeClient-001").start();
	}
	
}
