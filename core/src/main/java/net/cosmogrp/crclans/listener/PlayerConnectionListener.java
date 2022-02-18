package net.cosmogrp.crclans.listener;

import net.cosmogrp.crclans.server.ServerSender;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class PlayerConnectionListener implements Listener {

    @Inject private UserService userService;
    @Inject private ClanUserService clanUserService;
    @Inject private ClusteredUserRegistry clusteredUserRegistry;
    @Inject private ServerSender serverSender;

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
        clusteredUserRegistry.create(player);

        User user = userService.getUser(player);

        if (user != null) {
            clanUserService.connect(player, user);
        }

        serverSender.checkTeleport(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        clusteredUserRegistry.delete(player);
        User user = userService.saveUser(player);;

        if (user != null) {
            clanUserService.disconnect(user);
        }
    }

}
