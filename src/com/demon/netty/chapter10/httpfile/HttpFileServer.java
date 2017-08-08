package com.demon.netty.chapter10.httpfile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 文件服务器
 * @author xuliang
 * @since 2017年8月7日 上午10:44:47
 *
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "/src/com/demon/netty/";
    
    public void run(final int port, final String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 添加 HTTP请求消息编码器
                        ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                        /*
                         *  添加 HttpObjectAggregator 聚合器，作用是将多个消息转换为单一的 FullHttpRequest 或 FullHttpResponse
                         *  原因是 HTTP解码器在每个 HTTP消息中会生成多个消息对象：HttpRequest/HttpResponse HttpContent LastHttpContent
                         */
                        ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                        // 添加 HTTP响应消息编码器
                        ch.pipeline().addLast("http-encode", new HttpRequestEncoder());
                        // 添加 ChunkedWriteHandler，它的作用是支持异步发送大的码流（例如大的文件传输），但不占用过多的内存，防止发生内存溢出错误
                        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                        ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
                    }
                });
            ChannelFuture f = b.bind(port).sync();
            System.out.println("HTTP 文件服务器启动，网址是： http://127.0.0.1:"+port+url);
            f.channel().closeFuture().sync();
            
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new HttpFileServer().run(8080, DEFAULT_URL);
    }
    
}
