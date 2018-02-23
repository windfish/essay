package com.demon.netty.chapter14;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 心跳发送 ChannelHandler<br>
 * 握手成功之后，由客户端主动发送心跳消息，服务端接收到心跳消息之后，返回心跳应答消息<br>
 * 心跳消息的目的是为了检测链路的可用性，因此不需要携带消息体
 * @author xuliang
 * @since 2018/2/23 11:40
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {
    private volatile ScheduledFuture<?> heartbeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 握手成功，主动发送心跳消息
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.code()){
            // 握手成功，则启动无限循环定时器用于定期发送心跳消息。由于NioEventLoop 是一个schedule ，因此它支持定时器的执行
            heartbeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 10, TimeUnit.SECONDS);
        }else if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.code()){
            // 接收心跳响应的消息
            System.out.println("Client receive server heart beat message: ---> " + message);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 心跳定时器，用于发送心跳消息
     */
    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage heartbeat = buildHeartBeat();
            System.out.println("Client send heart beat message to server: ---> " + heartbeat);
            ctx.writeAndFlush(heartbeat);
        }
    }

    private NettyMessage buildHeartBeat(){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType((byte) MessageType.HEARTBEAT_REQ.code());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(heartbeat != null){
            heartbeat.cancel(true);
            heartbeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }

}
