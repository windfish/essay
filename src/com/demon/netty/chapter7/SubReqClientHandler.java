package com.demon.netty.chapter7;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 上午11:50:37
 *
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i=0;i<10;i++){
            ctx.write(subReq(i));
        }
        ctx.flush();
    }
    
    
    private SubscribeReq subReq(int i){
        SubscribeReq req = new SubscribeReq();
        req.setSubReqID(i);
        req.setUsername("test");
        req.setPhoneNumber("18888888888");
        req.setProductName("Netty 权威指南");
        req.setAddress("杭州市下沙新加坡科技园");
        return req;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Recive server response: "+msg);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
