package net.cosmogrp.crclans.redis;

import com.google.gson.Gson;
import net.cosmogrp.crclans.messenger.Messenger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Closeable;

public interface Redis extends Closeable {

    Messenger getMessenger();

    JedisPool getRawConnection();

    Jedis getListenerConnection();

    interface Builder {

        Builder setParentChannel(String parentChannel);

        Builder setServerId(String id);

        Builder setGson(Gson gson);

        Builder setJedisPool(JedisPool jedisPool);

        Builder setJedis(JedisInstance jedis);

        Builder setListenerConnection(Jedis listenerConnection);

        Redis build();

    }

}
