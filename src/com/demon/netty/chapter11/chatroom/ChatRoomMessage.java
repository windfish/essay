package com.demon.netty.chapter11.chatroom;

import java.io.Serializable;

/**
 * 
 * @author xuliang
 * @since 2017年12月3日 下午4:38:43
 *
 */
public class ChatRoomMessage implements Serializable {

	private static final long serialVersionUID = -6364363545613655170L;

	public Integer cmd;
	public Long playerId;
	public String data;
	
}
