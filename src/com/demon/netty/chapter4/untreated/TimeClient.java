package com.demon.netty.chapter4.untreated;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端
 * @author xuliang
 * @since 2017年2月6日 下午4:17:22
 *
 */
public class TimeClient {

	public void connect(String host, int port) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
					    // 当创建 NioSocketChannel 成功后，在初始化时，将 ChannelHandler设置到 ChannelPipeline中，用于处理网络I/O事件
						ch.pipeline().addLast(new TimeClientHandler());
					}
				});
			ChannelFuture f = b.connect(host, port).sync(); // 发起异步连接，并同步等待连接成功
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new TimeClient().connect("127.0.0.1", 8080);
	}
	
}
