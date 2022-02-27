package net.cosmogrp.crclans.listener;

import net.cosmogrp.crclans.user.PlayerService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class PlayerConnectionListener implements Listener {

    @Inject private PlayerService playerService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerService.load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerService.save(event.getPlayer());
    }

}
