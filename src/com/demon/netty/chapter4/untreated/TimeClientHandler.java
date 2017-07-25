package com.demon.netty.chapter4.untreated;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author xuliang
 * @since 2017年2月6日 下午4:27:45
 *
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

	private byte[] req;
	
	private int counter;
	
	public TimeClientHandler() {
		req = ("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    ByteBuf firstMessage;
	    for(int i=0;i<100;i++){
	        firstMessage = Unpooled.buffer(req.length);
	        firstMessage.writeBytes(req);
	        // 当客户端和服务端 TCP链路建立成功之后，Netty的 NIO线程会调用 channelActive方法，
	        // 调用 ChannelHandlerContext的 writeAndFlush方法将请求消息发送给服务端
	        ctx.writeAndFlush(firstMessage);
	    }
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    // 当服务端返回应答消息时，channelRead方法被调用
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("Now is : "+body+" ; the counter is "+ ++counter);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    // 异常时，exceptionCaught会被调用
		cause.printStackTrace();
		ctx.close();
	}
	
}
