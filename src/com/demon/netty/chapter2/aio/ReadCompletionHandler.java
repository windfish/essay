package com.demon.netty.chapter2.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * 
 * @author xuliang
 * @since 2017年2月6日 上午11:38:57
 *
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel channel) {
		if(this.channel == null){
			this.channel = channel;
		}
	}
	
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		// 获取客户端的消息内容，并返回服务端的应答消息
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		try {
			String req = new String(body, "UTF-8");
			System.out.println("The time server receive order : "+req);
			String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date().toString() : "BAD ORDER";
			doWrite(currentTime);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void doWrite(String currentTime){
		if(currentTime != null && !currentTime.isEmpty()){
			byte[] bytes = currentTime.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer, writeBuffer, 
						new CompletionHandler<Integer, ByteBuffer>() {
							@Override
							public void completed(Integer result, ByteBuffer attachment) {
								// 如果没有发完，继续发送
								if(attachment.hasRemaining()){
									channel.write(attachment, attachment, this);
								}
							}
							@Override
							public void failed(Throwable exc, ByteBuffer attachment) {
								try {
									channel.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		/**
		 * 发送异常时，对异常Throwable进行判断，如果是I/O异常，则关闭链路，释放资源
		 * 如果是其他异常，按照业务自己的逻辑进行处理
		 */
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
