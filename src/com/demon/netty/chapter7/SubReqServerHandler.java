package com.demon.netty.chapter7;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 上午10:55:59
 *
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;
        if("test".equalsIgnoreCase(req.getUsername())){
            System.out.println("Server accept client subscribe req: " + req.toString());
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }
    
    private SubscribeResp resp(int reqID){
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqID(reqID);
        resp.setDesc("Netty book order succed, 3 days later, sent to the designated address.");
        return resp;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
