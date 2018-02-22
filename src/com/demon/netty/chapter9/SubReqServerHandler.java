package com.demon.netty.chapter9;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 下午4:51:36
 *
 */
public class SubReqServerHandler extends SimpleChannelInboundHandler<SubscribeReq> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, SubscribeReq msg) throws Exception {
        System.out.println("Server accept client req: ["+msg.toString()+"]");
        ctx.writeAndFlush(resp(msg.getSubReqID()));
    }

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        SubscribeReq req = (SubscribeReq) msg;
//        System.out.println(req.getUsername()+" | "+req.getSubReqID());
//        if("test".equalsIgnoreCase(req.getUsername())){
            System.out.println("Server accept client req: ["+req.toString()+"]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
//        }
    }*/

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
