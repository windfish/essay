package com.demon.netty.chapter12;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * 
 * @author xuliang
 * @since 2017年12月5日 下午4:26:01
 *
 */
public class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    // 谚语列表
    private static final String[] DICTIONARY = {};
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        
    }
    
}
