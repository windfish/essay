package com.demon.netty.chapter14;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 握手认证的客户端 ChannelHandler，用于在通道激活时发起握手请求<br>
 * 握手的发起是在客户端和服务端 TCP 链路建立成功通道激活时，握手消息的接入和安全认证在服务端处理
 * @author xuliang
 * @since 2018/2/23 10:25
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当客户端和服务器 TCP 链路建立成功通道激活时，由客户端构造握手请求消息发送给服务端
        NettyMessage loginReq = buildLoginReq();
        ctx.writeAndFlush(loginReq);
        System.out.println("Send login request: " + loginReq);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /*
         对握手应答消息进行处理，首先判断是否是握手应答消息，如果不是，直接透传给后面的 ChannelHandler 进行处理
         如果是握手应答消息，则对应答结果进行判断，如果非0，说明认证失败，关闭链路，重新发起链接
          */
        NettyMessage message = (NettyMessage) msg;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.code()){
            byte loginResult = (byte) message.getBody();
            if(loginResult != (byte) 0){
                // 握手失败，关闭连接
                ctx.close();
            }else {
                System.out.println("Login is ok: "+ message);
                ctx.fireChannelRead(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq(){
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        header.setType((byte) MessageType.LOGIN_REQ.code());
        msg.setHeader(header);
        msg.setBody("login auth request");
        return msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
