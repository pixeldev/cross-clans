package net.cosmogrp.crclans.messenger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.cosmogrp.crclans.channel.Channel;
import net.cosmogrp.crclans.channel.RedisChannel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisMessenger implements Messenger {

    private final String serverId;
    private final Gson gson;

    private final JedisPool messengerPool;

    private final Map<String, RedisChannel<?>> channels;

    private final JedisPubSub pubSub;

    public RedisMessenger(
            String parentChannel, String serverId,
            Plugin plugin, Gson gson,
            JedisPool messengerPool,
            Jedis listenerConnection
    ) {
        this.serverId = serverId;
        this.gson = gson;
        this.messengerPool = messengerPool;

        this.channels = new ConcurrentHashMap<>();
        pubSub = new RedisSubChannelPubsub(parentChannel, serverId, gson, channels);

        Bukkit.getScheduler().runTaskAsynchronously(
                plugin,
                () -> listenerConnection.subscribe(pubSub, parentChannel)
        );
    }

    @Override
    public <T> Channel<T> getChannel(String name, TypeToken<T> type) {
        @SuppressWarnings("unchecked")
        RedisChannel<T> channel = (RedisChannel<T>) channels.get(name);

        if (channel == null) {
            channel = new RedisChannel<>(
                    serverId, name, type, this, messengerPool, gson
            );

            channels.put(name, channel);
        } else {
            if (!channel.getType().equals(type)) {
                throw new IllegalArgumentException(
                        "Channel type mismatch"
                );
            }
        }

        return channel;
    }

    @Override
    public void close() {
        channels.clear();

        if (pubSub.isSubscribed()) {
            pubSub.unsubscribe();
        }
    }
}
