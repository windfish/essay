package com.demon.hadoop.custom.rpc.bootstrap;

import com.demon.hadoop.custom.rpc.common.ProxyFactory;
import com.demon.hadoop.custom.rpc.service.HelloService;

/**
 * 客户端 测试
 */
public class NettyRpcClientBootstrap {

    public static void main(String[] args) {
        HelloService serviceProxy = ProxyFactory.create(HelloService.class);

        String result = serviceProxy.hello("custom rpc test");
        System.out.println("client proxy service result: " + result);
    }
}
