package com.demon.hadoop.custom.rpc_simple.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Netty RPC 服务端
 */
public class Server {

    private String host;
    private int port;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 对外公开的方法
     */
    public void start(){
        start0();
    }

    private void start0(){
        // 处理 accept 事件的线程工作组
        NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        // 处理 read/write 事件的线程工作组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline cpl = socketChannel.pipeline();
                        cpl.addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new ServerHandler());
                    }
                });
        try{
            ChannelFuture f = b.bind(host, port).sync();
            System.out.println("Server is started on "+ host + ":" + port);

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
