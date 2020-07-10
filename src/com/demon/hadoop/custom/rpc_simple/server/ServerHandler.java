package com.demon.hadoop.custom.rpc_simple.server;

import com.demon.hadoop.custom.rpc_simple.service.impl.DateTimeServiceImpl;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server has received message: " + msg);
        String name = msg.toString();
        String result = new DateTimeServiceImpl().hello(name);
        ctx.writeAndFlush(result + System.getProperty("line.separator"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
