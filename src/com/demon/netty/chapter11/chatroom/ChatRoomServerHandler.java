package com.demon.netty.chapter11.chatroom;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demon.util.DateUtil;
import com.demon.util.JsonUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

/**
 * 
 * @author xuliang
 * @since 2017年12月3日 下午2:28:42
 *
 */
public class ChatRoomServerHandler extends SimpleChannelInboundHandler<Object> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private WebSocketServerHandshaker handshaker;
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof FullHttpRequest){
			handlerHttpRequest(ctx, (FullHttpRequest) msg); 
		}
		else if(msg instanceof WebSocketFrame){
			handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
		logger.info("handlerHttpRequest req:{}", JsonUtil.toJsonString(req));
		System.out.println("handlerHttpRequest req:"+ JsonUtil.toJsonString(req));
		if(!req.getDecoderResult().isSuccess()
				|| !"websocket".equals(req.headers().get("Upgrade"))){
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("xlws://localhost:10000/", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if(handshaker == null){
			WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		}else{
			handshaker.handshake(ctx.channel(), req);
		}
	}
	
	private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
		if(frame instanceof CloseWebSocketFrame){
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		if(frame instanceof PingWebSocketFrame){
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if(!(frame instanceof TextWebSocketFrame)){
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}
		
		String request = ((TextWebSocketFrame) frame).text();
		logger.info("received:{}", request);
		System.out.println("received:"+ request);
		
		ChatRoomMessage msg = JsonUtil.parseObject(request, ChatRoomMessage.class);
		logger.info("received msg:{}", JsonUtil.toJsonString(msg));
		if(msg == null){
			ctx.channel().write(new TextWebSocketFrame("消息格式有误,"+request));
			return;
		}
		
		if(msg.cmd.intValue() == ChatRoomOperation.online.code()){
			if(ChatRoomClientManager.getInstance().alreadyLogin(msg.playerId)){
				ctx.channel().write(new TextWebSocketFrame("您已登录过"));
				return;
			}
			if(msg.playerId == null || msg.playerId.longValue() <= 0){
				ctx.channel().write(new TextWebSocketFrame("匿名登录成功"));
				for(Channel ch: ChatRoomClientManager.getInstance().allClients()){
					if(ch.equals(ctx.channel())){
						continue;
					}
					ch.writeAndFlush(new TextWebSocketFrame("匿名玩家进入聊天室"));
				}
			}else{
				ChatRoomClientManager.getInstance().bindSocketToPlayer(msg.playerId, ctx.channel());
				ctx.channel().write(new TextWebSocketFrame("您已成功进入聊天室"));
				for(Channel ch: ChatRoomClientManager.getInstance().allClients()){
					if(ch.equals(ctx.channel())){
						continue;
					}
					ch.writeAndFlush(new TextWebSocketFrame("玩家："+msg.playerId+" 进入聊天室"));
				}
			}
			
		}else if(msg.cmd.intValue() == ChatRoomOperation.offline.code()){
			ChatRoomClientManager.getInstance().removeClient(ctx.channel());
			ChatRoomClientManager.getInstance().releaseSocketByPlayerId(msg.playerId);
			
		}else{
			for(Channel ch: ChatRoomClientManager.getInstance().allClients()){
				if(ch.equals(ctx.channel())){
					ch.writeAndFlush(new TextWebSocketFrame("您("+DateUtil.dateToString(new Date(), DateUtil.STRING_DATE_FORMAT)+"): "+msg.data));
				}else{
					if(msg.playerId == null || msg.playerId.longValue() <= 0){
						ch.writeAndFlush(new TextWebSocketFrame("匿名玩家 ("+DateUtil.dateToString(new Date(), DateUtil.STRING_DATE_FORMAT)+"): "+msg.data));
					}else{
						ch.writeAndFlush(new TextWebSocketFrame("玩家："+msg.playerId+" ("+DateUtil.dateToString(new Date(), DateUtil.STRING_DATE_FORMAT)+"): "+msg.data));
					}
				}
			}
		}
	}
	
	private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp){
		if(resp.getStatus().code() != 200){
			ByteBuf buf = Unpooled.copiedBuffer(resp.getStatus().toString(), CharsetUtil.UTF_8);
			resp.content().writeBytes(buf);
			buf.release();
			resp.headers().set(HttpHeaders.Names.CONTENT_LENGTH, resp.content().readableBytes());
		}
		ChannelFuture f = ctx.channel().writeAndFlush(resp);
		if(!HttpHeaders.isKeepAlive(req) || resp.getStatus().code() != 200){
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ChatRoomClientManager.getInstance().addClient(ctx.channel());
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		ChatRoomClientManager.getInstance().removeClient(ctx.channel());
		ChatRoomClientManager.getInstance().releaseSocketByChannel(ctx.channel());
	}

}
