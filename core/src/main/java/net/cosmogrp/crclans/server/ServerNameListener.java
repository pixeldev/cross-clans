package net.cosmogrp.crclans.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class ServerNameListener
        implements PluginMessageListener {

    public static final String CHANNEL = "clans-server-name";

    @Inject private RedisCache redisCache;

    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte[] message
    ) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals(CHANNEL)) {
            String serverName = in.readUTF();
            redisCache.set(
                    "players-by-server",
                    player.getUniqueId().toString(),
                    serverName
            );
        }
    }
}
