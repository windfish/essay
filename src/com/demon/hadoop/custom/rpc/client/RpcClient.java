package com.demon.hadoop.custom.rpc.client;

import com.demon.hadoop.custom.rpc.protocol.RpcRequest;
import com.demon.hadoop.custom.rpc.protocol.RpcResponse;

/**
 * 客户端接口
 */
public interface RpcClient {

    /**
     * 发送请求
     */
    RpcResponse send(RpcRequest request);

    /**
     * 连接服务器
     */
    void connect();

    /**
     * 关闭客户端
     */
    void close();
}
