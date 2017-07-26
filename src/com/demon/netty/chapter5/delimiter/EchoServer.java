package com.demon.netty.chapter5.delimiter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 
 * @author xuliang
 * @since 2017年7月26日 上午10:07:46
 *
 */
public class EchoServer {

    public void bind(int port) throws Exception {
        // 配置服务端 NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 创建分隔符缓冲对象
                        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                        // 创建分隔符解码器对象，第一个参数代表数据流的最大长度，如果超过最大长度还没有分隔符，就抛出异常，防止内存溢出；第二个参数是分隔符缓冲对象
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter)); 
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new EchoServerHandler());
                    }
                });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
        int port = 8080;
        new EchoServer().bind(port);
    }
    
}
