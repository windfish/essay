package com.demon.test.iotest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author xuliang
 * @since 2018年10月19日 上午10:02:14
 *
 */
public class NioTest {

    public static void main(String[] args) throws UnknownHostException, IOException {
        NioServer server = new NioServer();
        server.start();
        try(Socket client = new Socket(InetAddress.getLocalHost(), 11111)){
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            reader.lines().forEach(line -> System.out.println(line));
            
        }
    }
    
}

class NioServer extends Thread {
    
    @Override
    public void run() {
        try(
                // 创建 Selector 和 Channel
                Selector selector = Selector.open();
                ServerSocketChannel serverSocket = ServerSocketChannel.open();
           ){
            // 绑定一个端口
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 11111));
            serverSocket.configureBlocking(false);
            // 注册到Selector 上，并说明关注点
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            while(true){
                selector.select(); // 阻塞等待就绪的Channel，这是关键点之一
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    sayHello((ServerSocketChannel) key.channel());
                    iterator.remove();
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sayHello(ServerSocketChannel server) throws IOException {
        try(SocketChannel client = server.accept()){
            client.write(Charset.defaultCharset().encode("Hello word. NIO "));
        }
    }
    
}
