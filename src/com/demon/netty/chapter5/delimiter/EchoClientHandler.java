package com.demon.netty.chapter5.delimiter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author xuliang
 * @since 2017年7月26日 上午11:50:28
 *
 */
public class EchoClientHandler extends ChannelHandlerAdapter {

    private int counter;
    
    private String echoReq = "Hi, welcome to Netty.";
    private String delimiter = "$_";
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i=1;i<=10;i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer((echoReq+i+delimiter).getBytes()));
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("This is "+ ++counter + " times receive server: ["+msg+"]");
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
