package com.demon.netty.chapter7;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 服务端，接收 POJO对象
 * @author xuliang
 * @since 2017年8月3日 上午10:38:31
 *
 */
public class SubReqServer {

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /*
                         *  ObjectDecoder 负责对 POJO对象进行解码，有多个构造函数，支持不同的 ClassResolver类解析器。
                         *  使用 weakCachingConcurrentResolver创建线程安全的 WeakReferenceMap（弱引用Map） 对类加载器进行缓存，
                         *  它支持多线程并发访问，当虚拟机内存不足时，会释放缓存中的内存，防止内存泄漏。
                         *  为了防止异常码流和解码错位导致的内存溢出，这里将单个对象最大序列化后的字节数组长度设置为1M
                         */
                        ch.pipeline().addLast(new ObjectDecoder(
                                1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        /*
                         * ObjectEncoder 可以在消息发送时自动将 POJO对象进行编码
                         */
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new SubReqServerHandler());
                    }
                });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
            
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
        int port = 8080;
        new SubReqServer().bind(port);
    }
    
}
