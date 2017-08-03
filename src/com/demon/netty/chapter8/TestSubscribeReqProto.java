package com.demon.netty.chapter8;

import java.util.ArrayList;

import com.demon.netty.chapter8.SubscribeReqProto.SubscribeReq;
import com.demon.netty.chapter8.SubscribeReqProto.SubscribeReq.Builder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.tools.javac.util.List;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 下午3:59:41
 *
 */
public class TestSubscribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req){
        return req.toByteArray();
    }
    
    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException{
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }
    
    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("test");
        builder.setProductName("Netty 权威指南");
        builder.setAddress("地址");
        return builder.build();
    }
    
    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReq req = createSubscribeReq();
        System.out.println("before encode: "+req.toString());
        SubscribeReq req1 = decode(encode(req));
        System.out.println("after encode: "+req1.toString());
        System.out.println("Assert equal: "+req1.equals(req));
    }
    
}
