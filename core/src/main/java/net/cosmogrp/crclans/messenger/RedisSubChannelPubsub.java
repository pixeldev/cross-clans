package net.cosmogrp.crclans.messenger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cosmogrp.crclans.channel.RedisChannel;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;

public class RedisSubChannelPubsub extends JedisPubSub {

    private final String parentChannel;
    private final String serverId;
    private final Gson gson;
    private final Map<String, RedisChannel<?>> channels;

    public RedisSubChannelPubsub(
            String parentChannel, String serverId,
            Gson gson,
            Map<String, RedisChannel<?>> channels
    ) {
        this.parentChannel = parentChannel;
        this.serverId = serverId;
        this.gson = gson;
        this.channels = channels;
    }

    @Override
    public void onMessage(String channel, String message) {
        // we don't care if the message isn't from the parent channel
        if (!channel.equals(parentChannel)) {
            return;
        }

        // we can parse the message as a json object
        JsonObject jsonMessage = JsonParser
                .parseString(message)
                .getAsJsonObject();

        String serverId = jsonMessage.get("server").getAsString();

        // if the message is from the server we're listening to
        if (!serverId.equals(this.serverId)) {
            return;
        }

        JsonElement targetServerElement = jsonMessage.get("targetServer");

        if (targetServerElement != null) {
            String targetServer = targetServerElement.getAsString();

            // if the message isn't for this server, ignore it
            if (!targetServer.equals(serverId)) {
                return;
            }
        }

        String subChannel = jsonMessage.get("channel").getAsString();

        @SuppressWarnings("unchecked")
        RedisChannel<Object> channelObject =
                (RedisChannel<Object>) channels.get(subChannel);

        // if the channel doesn't exist, we can't do anything
        if (channelObject == null) {
            return;
        }

        JsonElement object = jsonMessage.get("object");
        Object deserializedObject = gson.fromJson(
                object,
                channelObject.getType().getType()
        );

        channelObject.listen(serverId, deserializedObject);
    }
}
