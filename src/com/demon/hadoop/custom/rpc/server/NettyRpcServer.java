package com.demon.hadoop.custom.rpc.server;

import com.demon.hadoop.custom.rpc.codec.JsonSerialization;
import com.demon.hadoop.custom.rpc.codec.RpcDecoder;
import com.demon.hadoop.custom.rpc.codec.RpcEncoder;
import com.demon.hadoop.custom.rpc.common.Properties;
import com.demon.hadoop.custom.rpc.protocol.RpcRequest;
import com.demon.hadoop.custom.rpc.protocol.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * netty rpc 服务端
 */
public class NettyRpcServer {

    private String host;
    private int port;

    public NettyRpcServer() {
        this.host = Properties.HOST;
        this.port = Properties.PORT;
    }

    public NettyRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void serverStart(){
        serverStart0();
    }

    private void serverStart0(){
        ServerBootstrap bootstrap = initServerBootstrap();
        try {
            bootstrap.bind(host, port).sync().channel().closeFuture().channel();
        } catch (InterruptedException e) {
            System.out.println("netty server start error!");
            e.printStackTrace();
        }
        System.out.println("server start...");
    }

    private ServerBootstrap initServerBootstrap(){
        // 服务端启动类
        ServerBootstrap bootstrap = new ServerBootstrap();

        // 服务端的两组线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler((LogLevel.DEBUG)))
                .childHandler(initChannelInitializer());

        return bootstrap;
    }

    private ChannelInitializer initChannelInitializer(){
        return new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline cpl = channel.pipeline();
                // 处理TCP 请求中粘包的coder
                cpl.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4))
                        // 序列化和反序列化的coder
                        .addLast(new RpcEncoder(RpcResponse.class, new JsonSerialization()))
                        .addLast(new RpcDecoder(RpcRequest.class, new JsonSerialization()))
                        // 具体的逻辑处理handler
                        .addLast(initChannelHandler());
            }
        };
    }

    private ChannelHandler initChannelHandler(){
        return new NettyRpcServerHandler();
    }
}
