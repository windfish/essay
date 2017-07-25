package com.demon.netty.chapter4.untreated;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 未考虑 TCP粘包的服务端处理
 * @author xuliang
 * @since 2017年7月25日 上午10:10:44
 *
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()]; // readableBytes() 获取缓冲区可读的字节数
		buf.readBytes(req);   // readBytes 将缓冲区的字节数组复制到新建的数组中
		String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length()); // 发送的消息末尾加入换行符来进行消息的分割
		System.out.println("The time server receive order : "+body+" ; the counter is: "+ ++counter);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.write(resp); // 只写入缓存，不写入SocketChannel
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush(); // 将消息发送队列中的消息写入到SocketChannel中发送给对方
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
	
}
