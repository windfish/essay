package com.demon.netty.chapter3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端
 * @author xuliang
 * @since 2017年2月6日 下午3:25:10
 *
 */
public class TimeServer {

	/**
	 * Netty 内部会构造两个线程池，一个线程池只处理accept 请求，另一个线程池只处理read/write 请求
	 */
	public void bind(int port) throws Exception {
		// 配置服务端NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();	// 处理accept 请求
		EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理read/write 请求
		try {
			ServerBootstrap b = new ServerBootstrap(); // Netty用于启动NIO的辅助启动类
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class) // 设置创建的Channel的类型
				.option(ChannelOption.SO_BACKLOG, 1024) // 设置Channel的TCP参数
				.childHandler(new ChildChannelHandler()); // 绑定IO事件的处理类
			// 绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync(); // 等待服务端链路关闭之后main函数才退出
		} finally {
			// 退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new TimeServerHandler());
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		new TimeServer().bind(port);
	}
	
}
