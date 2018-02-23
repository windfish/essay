package com.demon.netty.chapter14;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 握手认证服务端 ChannelHandler
 * @author xuliang
 * @since 2018/2/23 11:24
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {
    private Map<String, Boolean> nodeCheck = new HashMap<>();
    private String[] whiteList = {"127.0.0.1", "192.168.10.58"};    // IP 白名单

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive message: " + msg);
        NettyMessage message = (NettyMessage) msg;
        // 如果是握手请求消息，处理；其他消息透传
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_REQ.code()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            // 重复登录
            if(nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte) -1);
            }else{
                // 获取客户端 IP
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                // IP 白名单校验
                boolean isOK = false;
                for(String whiteIP: whiteList){
                    if(whiteIP.equals(ip)){
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildResponse((byte) 0) : buildResponse((byte) -2);
                if(isOK){
                    nodeCheck.put(nodeIndex, true);
                }
                System.out.println("The login response is: " + loginResp + " body [" + loginResp.getBody() + "]");
                ctx.writeAndFlush(loginResp);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte result){
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        header.setType((byte) MessageType.LOGIN_RESP.code());
        msg.setHeader(header);
        msg.setBody(result);
        return msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当发生异常关闭链路时，需要将客户端的登录信息从登录缓存中去除，以保证后续客户端可以重连成功
        nodeCheck.remove(ctx.channel().remoteAddress().toString());     // 删除缓存
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

}
