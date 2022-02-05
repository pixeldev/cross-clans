package net.cosmogrp.crclans.redis;

import com.google.gson.Gson;
import net.cosmogrp.crclans.messenger.Messenger;
import net.cosmogrp.crclans.messenger.RedisMessenger;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

public class GsonRedis implements Redis {

    private final JedisPool jedisPool;
    private final Jedis listenerConnection;

    private Messenger messenger;

    protected GsonRedis(
            String parentChannel, String serverId,
            Plugin plugin, Gson gson,
            JedisPool jedisPool,
            Jedis listenerConnection
    ) {
        this.jedisPool = jedisPool;
        this.listenerConnection = listenerConnection;

        this.messenger = new RedisMessenger(
                parentChannel, serverId, plugin,
                gson, jedisPool, listenerConnection
        );
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }

    @Override
    public JedisPool getRawConnection() {
        return jedisPool;
    }

    @Override
    public Jedis getListenerConnection() {
        return listenerConnection;
    }

    @Override
    public void close() throws IOException {
        messenger.close();
        messenger = null;
        jedisPool.close();
        listenerConnection.close();
    }

    public static Builder builder(Plugin plugin) {
        return new RedisBuilder(plugin);
    }

    static class RedisBuilder implements Builder {

        private final Plugin plugin;

        private String parentChannel;
        private String serverId = UUID.randomUUID().toString();

        private Gson gson;
        private JedisPool jedisPool;
        private Jedis listenerConnection;

        public RedisBuilder(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public Builder setParentChannel(String parentChannel) {
            this.parentChannel = parentChannel;
            return this;
        }

        @Override
        public Builder setServerId(String id) {
            this.serverId = id;
            return this;
        }

        @Override
        public Builder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }

        @Override
        public Builder setJedisPool(JedisPool jedisPool) {
            this.jedisPool = jedisPool;
            return this;
        }

        @Override
        public Builder setJedis(JedisInstance jedis) {
            setJedisPool(jedis.pool());
            setListenerConnection(jedis.listenerConnection());

            return this;
        }

        @Override
        public Builder setListenerConnection(Jedis listenerConnection) {
            this.listenerConnection = listenerConnection;
            return this;
        }

        @Override
        public Redis build() {
            if (gson == null) {
                gson = new Gson();
            }

            return new GsonRedis(
                    parentChannel, serverId,
                    plugin, gson, jedisPool, listenerConnection
            );
        }
    }
}
