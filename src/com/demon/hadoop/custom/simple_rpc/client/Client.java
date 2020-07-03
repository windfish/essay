package com.demon.hadoop.custom.simple_rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Netty RPC 客户端
 */
public class Client {

    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化一个线程池，用来处理服务端返回的结果数据
    private static int cpu_cores = Runtime.getRuntime().availableProcessors();
    private static ExecutorService es = Executors.newFixedThreadPool(cpu_cores);

    private ClientHandler clientHandler;

    public void start(){
        start0();
    }

    private void start0(){
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        clientHandler = new ClientHandler();

        b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline cpl = socketChannel.pipeline();
                        cpl.addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(clientHandler);
                    }
                });
        try{
            b.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Object getProxy(final Class<?> clazz){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("客户端请求参数：" + args[0]);
                        clientHandler.setRequestParams(args[0].toString());
                        Future f = es.submit(clientHandler);
                        es.awaitTermination(1, TimeUnit.SECONDS);
                        System.out.println("future isDone: " + f.isDone());
                        Object o = f.get();
                        System.out.println("future result: " + o);
                        return o; // 返回执行结果，也就是服务端返回的结果
                    }
                });
    }
}

