package net.cosmogrp.crclans.listener;

import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class PlayerConnectionListener implements Listener {

    @Inject private RedisCache redisCache;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();

        redisCache.set(
                "players-by-server",
                playerId,
                Bukkit.getServer().getName()
        );

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
