package net.cosmogrp.crclans.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class PlayerConnectionListener implements Listener {

    @Inject private RedisCache redisCache;
    @Inject private UserService userService;
    @Inject private Plugin plugin;

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        String kickReason = userService.loadOrCreate(event.getUniqueId());

        if (kickReason != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickReason);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            @SuppressWarnings("UnstableApiUsage")
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("GetServer");

            player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
        }, 10);

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

        userService.saveUser(player);
    }

}
