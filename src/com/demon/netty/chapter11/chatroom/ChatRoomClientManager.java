package com.demon.netty.chapter11.chatroom;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author xuliang
 * @since 2017年12月3日 下午3:28:59
 *
 */
public class ChatRoomClientManager {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static ChatRoomClientManager instance = new ChatRoomClientManager();
	public static ChatRoomClientManager getInstance(){
		return instance;
	}
	
	/**未登录的socket*/
	private List<Channel> anonymousClients = new LinkedList<>();
	/**用户与socket对应关系*/
	private Map<Long, Channel> clientMap = new HashMap<>();
	
	public synchronized void addClient(Channel ch) {
		anonymousClients.add(ch);
	}
	
	public synchronized void removeClient(Channel ch) {
		anonymousClients.remove(ch);
	}
	
	public synchronized int size(){
		return anonymousClients.size();
	}
	
	public synchronized List<Channel> allClients(){
		return anonymousClients;
	}
	
	public synchronized void bindSocketToPlayer(Long playerId, Channel ch){
		clientMap.put(playerId, ch);
	}
	
	public synchronized void releaseSocketByPlayerId(Long playerId) {
		clientMap.remove(playerId);
	}
	
	public synchronized void releaseSocketByChannel(Channel ch) {
		logger.info("release channel:{}", ch.toString());
		if(clientMap.containsValue(ch)){
			for(Map.Entry<Long, Channel> entry: clientMap.entrySet()){
				if(entry.getValue().equals(ch)){
					clientMap.remove(entry.getKey());
					break;
				}
			}
		}
	}
	
	public synchronized boolean alreadyLogin(Long playerId) {
		return clientMap.containsKey(playerId);
	}
	
}
