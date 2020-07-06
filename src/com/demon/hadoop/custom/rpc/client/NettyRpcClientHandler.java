package com.demon.hadoop.custom.rpc.client;

import com.demon.hadoop.custom.rpc.common.RpcFuture;
import com.demon.hadoop.custom.rpc.protocol.RpcRequest;
import com.demon.hadoop.custom.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyRpcClientHandler extends ChannelHandlerAdapter {

    /**
     * 使用map 存储id 和future 的映射关系
     */
    private final Map<String, RpcFuture> futureMap = new ConcurrentHashMap<>();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof RpcRequest){
            RpcRequest request = (RpcRequest) msg;
            // 写数据时，增加映射关系
            futureMap.put(request.getRequestId(), new RpcFuture());
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof RpcResponse){
            System.out.println(msg);
            RpcResponse response = (RpcResponse) msg;
            // 读取服务端返回的数据时，将数据放入future 中
            RpcFuture future = futureMap.get(response.getRequestId());
            future.setResponse(response);
        }
        super.channelRead(ctx, msg);
    }

    /**
     * 根据requestId 获取response
     */
    public RpcResponse getRpcResponse(String requestId){
        try{
            RpcFuture future = futureMap.get(requestId);
            return future.getResponse(10);
        }finally {
            futureMap.remove(requestId);
        }
    }
}
