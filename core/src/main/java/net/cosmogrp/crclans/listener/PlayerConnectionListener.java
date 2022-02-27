package net.cosmogrp.crclans.listener;

import net.cosmogrp.crclans.server.ServerSender;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class PlayerConnectionListener implements Listener {

    @Inject private Plugin plugin;

    @Inject private UserService userService;
    @Inject private ClanUserService clanUserService;
    @Inject private ClusteredUserRegistry clusteredUserRegistry;
    @Inject private ServerSender serverSender;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> {
                    userService.loadOrCreate(player)
                            .whenComplete((user, throwable) -> {
                                if (user != null) {
                                    clanUserService.connect(player, user);
                                    clusteredUserRegistry.create(player);
                                }
                            });

                    serverSender.checkTeleport(player);
                },
                20L
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = userService.saveUser(player);
        clusteredUserRegistry.delete(player);

        if (user != null) {
            clanUserService.disconnect(user);
        }
    }

}
