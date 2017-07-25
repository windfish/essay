package com.demon.netty.chapter4.treated;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 支持 TCP粘包的服务端
 * @author xuliang
 * @since 2017年2月6日 下午3:25:10
 *
 */
public class TimeServer {

	public void bind(int port) throws Exception {
		// 配置服务端NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
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
		    // 增加两个解码器
		    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
		    ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new TimeServerHandler());
			/*
			 * LineBasedFrameDecoder 的工作原理是它依次遍历 ByteBuf中的可读字节，判断是否有“\n”或“\r\n”，
			 * 如果有，就以此位置为结束位置，从可读索引到结束位置区间的字节就组成一行。
			 * 它是以换行符为结束标志的解码器，支持携带结束符或不携带结束符两种编码方式，同时支持配置单行最大长度。
			 * 如果连续读取到最大长度后仍然没有发现换行符，就会抛出异常，同时忽略之前读到的异常码流。
			 * 
			 * StringDecoder 是将接收到的对象转换成字符串，然后继续调用后面的 handler
			 * 
			 * LineBasedFrameDecoder + StringDecoder 组合就是按行切换的文本解码器
			 */
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		new TimeServer().bind(port);
	}
	
}
