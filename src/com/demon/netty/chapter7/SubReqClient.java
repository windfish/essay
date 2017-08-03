package com.demon.netty.chapter7;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 客户端
 * @author xuliang
 * @since 2017年8月3日 上午11:32:41
 *
 */
public class SubReqClient {

    public void connect(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /*
                         * 禁止对类加载器进行缓存，它在基于 OSGi的动态模块化编程中经常使用
                         * 由于 OSGi的 bundle可以进行热部署和热升级，当某个 bundle升级后，它对应的类加载器也将一起升级
                         * 因此在动态模块化编程过程中，很少对类加载器进行缓存，因为它随时可能会发生变化
                         */
                        ch.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new SubReqClientHandler());
                    }
                });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
            
        }finally {
            group.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new SubReqClient().connect("127.0.0.1", 8080);
    }
    
}
