package com.demon.netty.chapter8;

import com.demon.netty.chapter8.SubscribeReqProto.SubscribeReq.Builder;

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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i=0;i<10;i++){
            ctx.write(subReq(i));
        }
        ctx.flush();
    }
    
    private SubscribeReqProto.SubscribeReq subReq(int i){
        Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(i);
        builder.setUserName("test");
        builder.setProductName("Netty 权威指南");
        builder.setAddress("地址");
        return builder.build();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client recive Server msg: ["+msg.toString()+"]");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
