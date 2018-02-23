package com.demon.netty.chapter14;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 * @author xuliang
 * @since 2018/2/23 14:38
 */
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(String host, int port) throws Exception {
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 为了防止由于单条消息过大导致的内存溢出或者畸形码流导致解码错位引起内存分配失败，对单条消息的最大长度进行了限制
//                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
//                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4, -8, 0));
//                            ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());     // 消息编码器
                            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));    // 读超时 Handler
                            ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());   // 握手请求 Handler
                            ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());   // 心跳消息 Handler

                            /*
                            利用 Netty 的 ChannelPipeline 和 ChannelHandler 机制，可以非常方便的实现功能的解耦和业务功能的定制
                            例如上面的心跳定时器、握手请求和后端的业务处理可以通过不同的 Handler 实现，类似于 AOP，但性能更高
                             */
                        }
                    });
            ChannelFuture f = b.connect(new InetSocketAddress(host, port), new InetSocketAddress(NettyConstant.LOCAL_HOST, NettyConstant.LOCAL_PORT)).sync();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
            // 释放完资源后，再次发起重连操作
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        try{
                            connect(NettyConstant.REMOTE_HOST, NettyConstant.REMOTE_PORT);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();;
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(NettyConstant.REMOTE_HOST, NettyConstant.REMOTE_PORT);
    }
}
