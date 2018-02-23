package com.demon.netty.chapter14;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息解码器<br>
 * LengthFieldBasedFrameDecoder 用来解析带有长度属性的包
 * @author xuliang
 * @since 2018/2/23 10:05
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private NettyMarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.marshallingDecoder = MarshallingCodeCFactory.buildCustomMarshallingDecoder();
    }

    /**
     *
     * @param maxFrameLength 消息最大长度，超过这个长度会报异常
     * @param lengthFieldOffset 长度属性的起始（偏移）位
     * @param lengthFieldLength 长度属性的长度
     * @param lengthAdjustment 长度调节值，在总长被定义为包含包头长度时，修正消息长度
     * @param initialBytesToStrip 跳过的字节数
     */
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        marshallingDecoder = MarshallingCodeCFactory.buildCustomMarshallingDecoder();
    }

    /*
        LengthFieldBasedFrameDecoder 解码器，支持自动的TCP 粘包和半包处理，只需要给出标识消息长度的字段偏移量和消息长度自身所占的字节数，Netty 就能自动实现对半包的处理
     */
    public Object decode(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        // 对于业务解码器来说，调用解码方法后，返回的就是整包数据或者空数据，如果是空数据，则说明是个半包消息，直接返回即可
        ByteBuf frame = (ByteBuf) super.decode(ctx, buf);
        if(frame == null){
            return null;
        }

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int size = frame.readInt();
        if(size > 0){
            Map<String, Object> attach = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i=0;i<size;i++){
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                buf.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attach.put(key, marshallingDecoder.decode(ctx, frame));
            }
            key = null;
            keyArray = null;
            header.setAttachment(attach);
        }
        if(frame.readableBytes() > 0){
            message.setBody(marshallingDecoder.decode(ctx, frame));
        }
        message.setHeader(header);
//        System.out.println("message decode end:" + message);
        return message;
    }

}
