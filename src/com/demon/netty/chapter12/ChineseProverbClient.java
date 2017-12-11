package com.demon.netty.chapter12;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * 
 * @author xuliang
 * @since 2017年12月11日 下午5:44:06
 *
 */
public class ChineseProverbClient {

    public void run(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChineseProverbClientHandler());
            Channel ch = b.bind(0).sync().channel();
            // 由于不需要和服务端建立链路，UDP channel创建完成后，客户端需要主动发广播消息
            // 向网段内所有机器广播UDP 消息
            ch.writeAndFlush(
                    new DatagramPacket(Unpooled.copiedBuffer("谚语词典查询?", CharsetUtil.UTF_8), 
                    new InetSocketAddress("255.255.255.255", port)));
            if(!ch.closeFuture().await(15000)){
                System.out.println("查询超时");
            }
        }finally {
            group.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new ChineseProverbClient().run(8080);
    }
    
}
