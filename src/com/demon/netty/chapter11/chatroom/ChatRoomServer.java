package com.demon.netty.chapter11.chatroom;

import com.demon.jsvc.DaemonJob;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocket 聊天室
 * @author xuliang
 * @since 2017年12月3日 下午2:08:08
 *
 */
public class ChatRoomServer extends DaemonJob {

	private void init(int port) throws Exception {
		EventLoopGroup mainGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(mainGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch)
							throws Exception {
						ch.pipeline().addLast("http-codes", new HttpServerCodec());
						ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65535));
						ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
						ch.pipeline().addLast("handler", new ChatRoomServerHandler());
					}
					
				});
			Channel ch = b.bind(port).sync().channel();
			System.out.println("chat room init success at port "+port);
			System.out.println("Open you browser and navigate to http://localhost:"+port+"/");
			ch.closeFuture().sync();
		}finally {
			mainGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	static{
	    // logback 日志方式，支持根据项目分别打印日志
	    System.setProperty("main_alias", "chatroom");
	}
	
	public static void main(String[] args) throws Exception {
		new ChatRoomServer().init(12345);
	}

    @Override
    public void start() throws Exception {
        // jsvc 方式启动，需实现 org.apache.commons.daemon.Daemon 接口
        new ChatRoomServer().init(10000);
    }
	
}
