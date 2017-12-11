package com.demon.netty.chapter12;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

/**
 * 
 * @author xuliang
 * @since 2017年12月5日 下午4:26:01
 *
 */
public class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    // 谚语列表
    private static final String[] DICTIONARY = {"只要功夫深铁杵磨成针","一寸光阴一寸金，寸金难买寸光阴","旧时王谢堂前燕，飞入寻常百姓家","老骥伏枥，志在千里；烈士暮年，壮心不已"};
    
    private String nextQuote(){
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        // 接收的消息，经过Netty 封装，为DatagramPacket 对象
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        if("谚语词典查询?".equals(req)){
            // DatagramPacket 有两个参数，第一个是要发送的内容，为ByteBuf，第二个是目的地址，包括IP和端口
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语查询结果："+nextQuote(), CharsetUtil.UTF_8), packet.sender()));
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
    
}
