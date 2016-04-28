package com.demon.redis;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {
	private Logger logger = Logger.getLogger(JedisUtil.class);
	
	private JedisUtil(){
		
	}
	
	private static class RedisUtilHelper {
		private static final JedisUtil instance = new JedisUtil();
	}
	
	public static JedisUtil getInstance(){
		return RedisUtilHelper.instance;
	}
	
	private static Map<String, JedisPool> maps = new HashMap<String, JedisPool>();
	
	private static JedisPool getPool(String ip, int port){
		String key = ip+":"+port;
		JedisPool pool = null;
		if(!maps.containsKey(key)){
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(RedisConfig.MAX_ACTIVE);
			config.setMaxIdle(RedisConfig.MAX_IDLE);
			config.setMaxWaitMillis(RedisConfig.MAX_WAIT);
			config.setTestOnBorrow(true);
			config.setTestOnReturn(true);
			
			pool = new JedisPool(config, ip, port, RedisConfig.TIME_OUT);
			maps.put(key, pool);
		}else{
			pool = maps.get(key);
		}
		return pool;
	}
	
	public Jedis getJedis(String ip, int port){
		Jedis jedis = null;
		int count = 0;
		do{
			try {
				jedis = getPool(ip, port).getResource();
			} catch (Exception e) {
				logger.error("get redis master1 error", e);
				getPool(ip, port).returnBrokenResource(jedis);
			}
			count++;
		}while(jedis==null&&count<RedisConfig.RETRY_NUM);
		return jedis;
	}
	
	public void closeJedis(Jedis jedis, String ip, int port){
		getPool(ip, port).returnResource(jedis);
	}
	
	private class RedisConfig {
		//可用连接实例的最大数目，默认值是8
		//如果赋值为-1，表示不限制；如果pool已分配了max_active个jedis实例，则pool的状态为exhausted(耗尽)
		public static final int MAX_ACTIVE = 1024;
		
		//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值是8
		public static final int MAX_IDLE = 200;
		
		//等待可用连接的最大等待时间，单位毫秒，默认值-1，表示永不超时。如果超过等待时间，则抛出JedisConnectionException
		public static final int MAX_WAIT = 10000;
		
		public static final int TIME_OUT = 10000;
		
		public static final int RETRY_NUM = 5;
	}
}
