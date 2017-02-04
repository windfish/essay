package com.demon.netty.chapter2.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用类
 * 负责轮询多路复用器Select，可以处理多个客户端的并发接入
 * 
 * @author xuliang
 * @since 2017年2月4日 下午4:05:52
 *
 */
public class MultiplexerTimeServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel serverChannel;
	private volatile boolean stop;
	
	/**
	 * 初始化多路复用器、绑定监听端口
	 * 
	 * @param port
	 */
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.bind(new InetSocketAddress(port), 1024);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port : "+port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop(){
		this.stop = true;
	}

	/**
	 * 循环遍历selector，休眠时间是1s。
	 * 当有处于就绪状态的Channel时，selector将返回就绪状态的Channel的SelectionKey集合，对集合进行迭代，可以进行异步读写操作
	 */
	@Override
	public void run() {
		while(!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				SelectionKey key = null;
				while(iterator.hasNext()){
					key = iterator.next();
					iterator.remove();
					try{
						handlerInput(key);
					}catch (Exception e) {
						if(key != null){
							key.cancel();
							if(key.channel() != null){
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 多路复用器关闭后，所有注册的Channel和Pipe 等资源都会自动去注册并关闭，不需要重复释放资源
		if(selector != null){
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handlerInput(SelectionKey key) throws IOException{
		if(key.isValid()){
			if(key.isAcceptable()){
				// 处理新接入的请求
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()){
				// 读取数据
				SocketChannel sc = (SocketChannel) key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes > 0){
					readBuffer.flip(); // 先将缓冲区当前位置设置为最大读取位置，在将缓冲区当前位置置为0，意思是读取时，读取从0到当前位置的数据
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order : "+body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
					doWrite(sc, currentTime);
				}else if(readBytes < 0){
					// 对端链路关闭
					key.cancel();
					sc.close();
				}else{
					; // 读到0字节，忽略
				}
			}
		}
	}
	
	private void doWrite(SocketChannel channel, String resp) throws IOException{
		if(resp != null && resp.trim().length() > 0){
			byte[] bytes = resp.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer);
			
			/**
			 * 由于SocketChannel 是异步非阻塞的，并不保证一次能够把字节数组发送完，此时会出现“写半包”的情况
			 * 需要注册写操作，不断轮询Selector将没有发送完的数据发送完，可通过ByteBuffer的hasRemaining()方法判断消息是否发送完
			 */
		}
	}

}
