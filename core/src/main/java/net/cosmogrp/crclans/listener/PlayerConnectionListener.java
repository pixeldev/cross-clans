package net.cosmogrp.crclans.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.cosmogrp.crclans.server.ServerNameListener;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class PlayerConnectionListener implements Listener {

    @Inject private RedisCache redisCache;
    @Inject private Plugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();

        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(ServerNameListener.CHANNEL);
        output.writeUTF("GetServer");

        player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());

        redisCache.set(
                "players-by-name",
                player.getName(),
                playerId
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        redisCache.del(
                "players-by-server",
                player.getUniqueId().toString()
        );

        redisCache.del(
                "players-by-name",
                player.getName()
        );
    }

}
