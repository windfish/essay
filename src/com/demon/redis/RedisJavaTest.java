package com.demon.redis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * Java 访问Redis
 */
public class RedisJavaTest {

	public static void main(String[] args) {
		//连接redis服务
		Jedis jedis = new Jedis("vm-ubuntu");
		System.out.println("connection to redis server successfully");
		
		//查看服务是否运行
		System.out.println("redis server is running: " + jedis.ping());
		
		System.out.println("------------String Test-----------------");
		//字符串
		jedis.set("name".getBytes(), "java client name".getBytes());
		System.out.println("redis String name is : "+jedis.get("name"));
		
		System.out.println("------------List Test-----------------");
		//列表
		jedis.lpush("java_list", "one", "two", "three");
		List<String> list = jedis.lrange("java_list", 0, -1);
		for(int i=0;i<list.size();i++){
			System.out.println("list ["+i+"] is: "+list.get(i));
		}
		
		System.out.println("------------keys Test-----------------");
		Set<String> keys = jedis.keys("*");
		Iterator<String> ite = keys.iterator();
		while(ite.hasNext()){
			System.out.println("redis keys is: "+ite.next());
		}
		
	}

}
