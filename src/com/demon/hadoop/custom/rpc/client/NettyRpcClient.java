package com.demon.hadoop.custom.rpc.client;

import com.demon.hadoop.custom.rpc.codec.JsonSerialization;
import com.demon.hadoop.custom.rpc.codec.RpcDecoder;
import com.demon.hadoop.custom.rpc.codec.RpcEncoder;
import com.demon.hadoop.custom.rpc.protocol.RpcRequest;
import com.demon.hadoop.custom.rpc.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * netty rpc 客户端
 */
public class NettyRpcClient implements RpcClient {

    private String host;
    private int port;

    private Channel channel;
    private NettyRpcClientHandler clientHandler;
    private NioEventLoopGroup nioEventLoopGroup;

    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void connect() {
        clientHandler = new NettyRpcClientHandler();
        nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();

        client.group(nioEventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(initChannelInitializer(clientHandler));

        try {
            channel = client.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            System.out.println("netty rpc client start error !");
            e.printStackTrace();
        }
    }

    private ChannelInitializer initChannelInitializer(NettyRpcClientHandler clientHandler){
        return new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline cpl = channel.pipeline();
                cpl.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4))
                        .addLast(new RpcEncoder(RpcRequest.class, new JsonSerialization()))
                        .addLast(new RpcDecoder(RpcResponse.class, new JsonSerialization()))
                        .addLast(clientHandler);
            }
        };
    }

    @Override
    public RpcResponse send(RpcRequest request) {
        try{
            System.out.println(request);
            channel.writeAndFlush(request).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 根据requestId 获取结果
        return clientHandler.getRpcResponse(request.getRequestId());
    }

    @Override
    public void close() {
        nioEventLoopGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }
}
