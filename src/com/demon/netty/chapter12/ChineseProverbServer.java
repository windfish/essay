package com.demon.netty.chapter12;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * 
 * @author xuliang
 * @since 2017年12月5日 下午4:22:22
 *
 */
public class ChineseProverbServer {

    public void init(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)   // 支持广播
                .handler(new ChineseProverbServerHandler());
            b.bind(port).sync().channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new ChineseProverbServer().init(8080);
    }
    
}
