package com.demon.netty.chapter2.pseudo_nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端
 * 
 * 伪异步IO：
 * 	通过线程池或消息队列实现一个或多个线程处理N个客户端的模型
 * 	服务端通过一个线程池来处理多个客户端的请求接入，形成客户端格式M：线程池最大线程数N的比例关系，其中M可以远远大于N
 * 	通过线程池可以灵活调配资源，设置线程最大值，防止由于海量并发接入导致线程耗尽
 * 	但底层还是使用阻塞IO，不能从根本上解决问题
 * 
 * @author xuliang
 * @since 2017年2月4日 上午11:57:16
 *
 */
public class TimeServer {

	public static void main(String[] args) throws IOException {
		int port = 8080;
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is start in port: "+port);
			Socket socket = null;
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);
			while(true){
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		} finally {
			if(server != null){
				System.out.println("The time server close");
				server.close();
				server = null;
			}
		}
	}
	
}
