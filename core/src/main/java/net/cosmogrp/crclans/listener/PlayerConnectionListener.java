package net.cosmogrp.crclans.listener;

import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class PlayerConnectionListener implements Listener {

    @Inject private RedisCache redisCache;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        redisCache.set(
                "players",
                event.getPlayer().getUniqueId().toString(),
                Bukkit.getServer().getName()
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        redisCache.del(
                "players",
                event.getPlayer().getUniqueId().toString()
        );
    }

}
