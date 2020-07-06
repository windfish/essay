package com.demon.hadoop.custom.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义反序列化机制
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;
    private Serialization serialization;

    public RpcDecoder(Class<?> clazz, Serialization serialization) {
        this.clazz = clazz;
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        System.out.println("RpcDecoder decode begin...");
        if(in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = serialization.deserialize(data, clazz);
        list.add(obj);
    }
}
