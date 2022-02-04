package net.cosmogrp.crclans.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public record JedisInstance(Jedis listenerConnection, JedisPool pool) {

    public Jedis getListenerConnection() {
        return listenerConnection;
    }

    public JedisPool getPool() {
        return pool;
    }

}
