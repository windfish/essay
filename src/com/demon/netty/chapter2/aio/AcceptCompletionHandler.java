package com.demon.netty.chapter2.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 
 * @author xuliang
 * @since 2017年2月4日 下午5:59:52
 *
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
		/**
		 *  重新绑定接收客户端连接的回调方法
		 *  如果有新的客户端接入，则会继续调用accept方法，每当接收一个客户端连接后，再异步接收新的客户端连接
		 *  形成一个AsynchronousSocketChannel接收多个客户端连接
		 */
		attachment.asyncSocketChannel.accept(attachment, this);
		
		// 接收客户端的请求消息
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result.read(buffer, // 接收缓冲区
					buffer, // 异步channel携带的附件，通知回调的时候作为入参使用
					new ReadCompletionHandler(result) // 接收通知回调的handler
					);
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

}
