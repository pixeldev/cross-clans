package net.cosmogrp.crclans.messenger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    private final Plugin plugin;
    private final Gson gson;

    private final JedisPool messengerPool;
    private final Jedis listenerConnection;

    private final Map<String, RedisChannel<?>> channels;

    private final JedisPubSub pubSub;

    public RedisMessenger(
            String serverId, Plugin plugin,
            Gson gson, JedisPool messengerPool,
            Jedis listenerConnection
    ) {
        this.serverId = serverId;
        this.plugin = plugin;
        this.gson = gson;
        this.messengerPool = messengerPool;
        this.listenerConnection = listenerConnection;

        this.channels = new ConcurrentHashMap<>();
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                @SuppressWarnings("unchecked")
                RedisChannel<Object> channelObject =
                        (RedisChannel<Object>) channels.get(channel);

                if (channelObject == null) {
                    return;
                }

                JsonObject jsonMessage = JsonParser
                        .parseString(message)
                        .getAsJsonObject();

                String serverId = jsonMessage.get("server").getAsString();

                // if the message is from the server we're listening to
                if (!serverId.equals(RedisMessenger.this.serverId)) {
                    return;
                }

                JsonElement object = jsonMessage.get("object");
                Object deserializedObject = gson.fromJson(
                        object,
                        channelObject.getType().getType()
                );

                channelObject.listen(serverId, deserializedObject);
            }
        };
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

            if (pubSub.isSubscribed()) {
                pubSub.subscribe(name);
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(
                        plugin,
                        () -> listenerConnection.subscribe(pubSub, name)
                );
            }
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
