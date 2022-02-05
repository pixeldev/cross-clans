package net.cosmogrp.crclans.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public record JedisInstance(Jedis listenerConnection, JedisPool pool) { }
