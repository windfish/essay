package com.demon.hadoop.custom.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义序列化机制
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clazz;
    private Serialization serialization;

    public RpcEncoder(Class<?> clazz, Serialization serialization) {
        this.clazz = clazz;
        this.serialization = serialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {
        System.out.println("RpcEncoder encode begin...");
        if(clazz != null){
            byte[] bytes = serialization.serialize(o);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
