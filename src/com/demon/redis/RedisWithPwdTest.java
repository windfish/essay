package com.demon.redis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisWithPwdTest {

    private static final String IP = "test.redis.uuuwin.com";
    private static final int PORT = 7480;
    private static final String pwd = "s7480.uw.redis";
    private Jedis jedis = null;

    @Before
    public void init() {
        jedis = JedisUtil.getInstance().getJedis(IP, PORT, pwd);
    }

    @After
    public void close() {
        JedisUtil.getInstance().closeJedis(jedis, IP, PORT);
    }
    
    @Test
    public void test(){
        jedis.set("test-jedis-java", "123132131");
        System.out.println(jedis.get("test-jedis-java"));
    }
    
}
