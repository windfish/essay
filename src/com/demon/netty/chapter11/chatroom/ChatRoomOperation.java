package com.demon.netty.chapter11.chatroom;

/**
 * 
 * @author xuliang
 * @since 2017年12月3日 下午4:11:44
 *
 */
public enum ChatRoomOperation {

	online(1),
	offline(2),
	
	send(10),
	;
	
	private int code;
	private ChatRoomOperation(int code) {
		this.code = code;
	}
	
	public int code() {
		return code;
	}
	
}
