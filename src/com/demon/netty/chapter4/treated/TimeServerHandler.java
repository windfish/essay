package com.demon.netty.chapter4.treated;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 支持 TCP粘包的服务端处理
 * @author xuliang
 * @since 2017年7月25日 上午10:10:44
 *
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    // 增加解码器后，接收的消息就是删除回车换行之后的数据，不需要考虑读半包的问题，也不需要对消息进行编码
		String body = (String) msg;
		System.out.println("The time server receive order : "+body+" ; the counter is: "+ ++counter);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
		currentTime += System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(resp);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
	
}
