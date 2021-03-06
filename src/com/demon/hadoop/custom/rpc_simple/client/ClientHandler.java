package com.demon.hadoop.custom.rpc_simple.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Callable;

/**
 * 客户端处理类
 */
public class ClientHandler extends ChannelHandlerAdapter implements Callable {

    private ChannelHandlerContext ctx;
    private String result;
    private String requestParams;

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    // 用来发送数据并接收服务端数据，并返回结果到带来对象方法中
    @Override
    public synchronized Object call() throws Exception {
        System.out.println("client handler call Invocated...");

        // 发送数据到服务端
        ctx.writeAndFlush(requestParams + System.getProperty("line.separator"));
        wait(); // 发送完请求后，等待服务端返回数据
        System.out.println("唤醒后，返回服务端数据-----" + this.result);
        return this.result; // 服务端返回数据后，将结果返回代理对象
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive 被调用...");
        this.ctx = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.result = msg.toString();
        System.out.println("client received result: " + this.result);

        notify(); // 唤醒call 返回数据
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
