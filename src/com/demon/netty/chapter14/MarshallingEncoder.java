package com.demon.netty.chapter14;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

/**
 * 消息编码工具类
 *
 * @author xuliang
 * @since 2018/2/12 15:23
 */
public class MarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    Marshaller marshaller;

    public MarshallingEncoder() {

    }

    public void encode(Object obj, ByteBuf buf){

    }
}
