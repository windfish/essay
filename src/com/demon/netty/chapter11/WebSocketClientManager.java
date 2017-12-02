package com.demon.netty.chapter11;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Administrator
 *
 */
public class WebSocketClientManager {
	
	private static WebSocketClientManager instance = new WebSocketClientManager();
	public static WebSocketClientManager getInstance(){
		return instance;
	}
	
	private List<Channel> clientChannels = new ArrayList<Channel>();
	
	public synchronized void addClient(Channel c) {
		clientChannels.add(c);
	}
	
	public synchronized void removeClient(Channel c) {
		clientChannels.remove(c);
	}
	
	public synchronized int size(){
		return clientChannels.size();
	}
	
	public synchronized List<Channel> allClients(){
		return clientChannels;
	}
	
}
