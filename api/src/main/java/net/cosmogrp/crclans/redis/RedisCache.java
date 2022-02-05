package net.cosmogrp.crclans.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCache {

    private final String name;
    private final JedisPool jedisPool;

    public RedisCache(String name, JedisPool jedisPool) {
        this.name = name;
        this.jedisPool = jedisPool;
    }

    public void set(String table, String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(name + ":" + table, key, value);
        }
    }

    public String get(String table, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(name + ":" + table, key);
        }
    }

    public void del(String table, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(name + ":" + table, key);
        }
    }
}
