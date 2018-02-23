package com.demon.netty.chapter9;

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
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        System.out.println("--------------------"+msg);
        SubscribeReq req = (SubscribeReq) msg;
        if("test".equalsIgnoreCase(req.getUsername())){
            System.out.println("Server accept client req: ["+req.toString()+"]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("read complete");
    }

    private SubscribeResp resp(int subReqID){
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqID(subReqID);
        resp.setDesc("Netty marshaller resp");
        return resp;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
}
