package com.demon.hadoop.custom.rpc.bootstrap;

import com.demon.hadoop.custom.rpc.server.NettyRpcServer;

/**
 * 服务端 测试
 */
public class NettyRpcServerBootstrap {

    public static void main(String[] args) {
        NettyRpcServer server = new NettyRpcServer();

        server.serverStart();
    }
}
