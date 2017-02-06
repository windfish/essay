package com.demon.netty.chapter2.aio;

/**
 * 客户端
 * @author xuliang
 * @since 2017年2月6日 下午1:50:57
 *
 */
public class TimeClient {

	public static void main(String[] args) {
		int port = 8080;
		new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-TimeClient-001").start();
	}
	
}
