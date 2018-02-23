package com.demon.netty.chapter14;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳应答 ChannelHandler
 * @author xuliang
 * @since 2018/2/23 14:25
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 返回心跳应答消息
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.code()){
            System.out.println("Receive client heart beat message: ---> " + message);
            NettyMessage heartbeat = buildHeartbeat();
            System.out.println("Send heart beat response message to client: ---> " + heartbeat);
            ctx.writeAndFlush(heartbeat);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeartbeat(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType((byte) MessageType.HEARTBEAT_RESP.code());
        message.setHeader(header);
        return message;
    }

}
