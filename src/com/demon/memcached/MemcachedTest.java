package com.demon.memcached;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedTest {
	private static MemCachedClient client = new MemCachedClient();
	
	static{
		/*
		 * 设置缓存服务器列表
		 */
		String[] servers = {
				"vm-ubuntu:11211"
		};
		/*
		 * 设置服务器权重
		 */
		Integer[] weights = {3};
		
		//创建一个socket连接池
		SockIOPool pool = SockIOPool.getInstance();
		
		//向连接池设置服务器和权重信息
		pool.setServers(servers);
		pool.setWeights(weights);
		
		//TCP设置
		pool.setNagle(false); //不使用Nagle算法
		pool.setSocketTO(3000); //设置读的超时时间为3s
		pool.setSocketConnectTO(0); //不设置连接的超时时间
		
		//初始化连接池
		pool.initialize();
	}

	public static void main(String[] args) {
		client.set("test", "memcached test in java");
		System.out.println(client.get("test").toString());
	}

}
