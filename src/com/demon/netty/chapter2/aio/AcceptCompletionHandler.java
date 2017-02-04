package com.demon.netty.chapter2.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 接收客户的连接，异步发送消息
 * @author xuliang
 * @since 2017年2月4日 下午5:59:52
 *
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
		
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		
	}

}
