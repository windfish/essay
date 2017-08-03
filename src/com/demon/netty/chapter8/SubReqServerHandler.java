package com.demon.netty.chapter8;

import com.demon.netty.chapter8.SubscribeReqProto.SubscribeReq;
import com.demon.netty.chapter8.SubscribeRespProto.SubscribeResp.Builder;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 下午4:51:36
 *
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req = (SubscribeReq) msg;
        if("test".equalsIgnoreCase(req.getUserName())){
            System.out.println("Server accept client req: ["+req.toString()+"]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }
    
    private SubscribeRespProto.SubscribeResp resp(int subReqID){
        Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqID(subReqID);
        builder.setRespCode(0);
        builder.setDesc("Netty book order succeed, later sent to designated address.");
        return builder.build();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
