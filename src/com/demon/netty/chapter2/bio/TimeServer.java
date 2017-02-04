package com.demon.netty.chapter2.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端
 * 
 * 阻塞式IO：
 * 	当有客户端连接时，创建一个新的线程处理客户端请求，一个线程只能处理一个客户端连接
 * 	在客户端并发连接增加时，只能通过扩展硬件资源满足服务需求，这种模型无法满足高性能、高并发接入的场景
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
			while(true){
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
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
