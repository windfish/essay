package com.demon.netty.chapter11;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocket 服务端
 * @author xuliang
 * @since 2017年11月29日 下午3:01:46
 *
 */
public class WebSocketServer {

	private static ExecutorService executor = Executors.newSingleThreadExecutor();
	
    public void run(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        /*HttpServerCodec 将请求和应答消息编码或者解码为HTTP 消息*/
                        pipeline.addLast("http-codec", new HttpServerCodec());
                        /*HttpObjectAggregator 它的目的是将HTTP 消息的多个部分组成一条完整的HTTP 消息*/
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                        /*ChunkedWriteHandler 用来向客户端发送HTML5 文件，它主要用于支持浏览器和服务端进行WebSocket 通信*/
                        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                        pipeline.addLast("handler", new WebSocketServerHandler());
                    }
                    
                });
            Channel ch = b.bind(port).sync().channel();
            System.out.println("WebSocket server started at port "+port);
            System.out.println("Open you browser and navigate to http://localhost:"+port+"/");
            ch.closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
    	// 往连接的客户端发消息
    	executor.submit(new Runnable() {
			@Override
			public void run() {
				while(true){
					List<Channel> clients = WebSocketClientManager.getInstance().allClients();
					for(Channel c: clients){
						c.writeAndFlush(new TextWebSocketFrame("服务器消息：现在时刻"+new Date()));
						System.out.println(c + " send");
					}
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
        new WebSocketServer().run(8080);
    }
    
}
