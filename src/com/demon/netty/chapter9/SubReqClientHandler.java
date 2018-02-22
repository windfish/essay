package com.demon.netty.chapter9;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 下午5:02:21
 *
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for(int i=0;i<10;i++){
            SubscribeReq req = subReq(i);
            ctx.writeAndFlush(req);
            System.out.println("send "+req.toString());
        }
//        ctx.flush();
    }
    
    private SubscribeReq subReq(int i){
        SubscribeReq req = new SubscribeReq();
        req.setSubReqID(i);
        req.setUsername("test");
        req.setProductName("Netty 权威指南 for marshaller");
        req.setAddress("地址");
        req.setPhoneNumber("135xxxxxxxx");
        return req;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Client recive Server msg: ["+msg.toString()+"]");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
}
