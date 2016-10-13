package com.jlh.core;

import redis.clients.jedis.Jedis;

public class RedisService {
    private int port = 5556;
    private String host = "127.0.0.1";
    private Jedis jedis = null;

    public RedisService() {
        jedis = new Jedis(host, port);
    }

    public String cache(String key, String value, int seconds) {
        String result = jedis.set(key, value);
        jedis.expire(key, seconds);
        return result;
    }

    public String get(String key) {
        return jedis.get(key);
    }
    public String set (String key,String value){
        return jedis.set(key,value);
    }
    public static void main(String[] args) throws InterruptedException {
        RedisService r= new RedisService();
        System.out.println (r.get("jlh1"));
        System.out.println (r.get("jlh1"));
        System.out.println (r.get("jlh1"));
        System.out.println (r.get("jlh1"));

    }
}
