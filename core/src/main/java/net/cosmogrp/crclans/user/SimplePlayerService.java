package net.cosmogrp.crclans.user;

import net.cosmogrp.crclans.server.ServerSender;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class SimplePlayerService implements PlayerService {

    @Inject private Plugin plugin;

    @Inject private UserService userService;
    @Inject private ClanUserService clanUserService;
    @Inject private ClusteredUserRegistry clusteredUserRegistry;
    @Inject private ServerSender serverSender;

    @Override
    public void load(Player player) {
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

    @Override
    public void save(Player player) {
        User user = userService.saveUser(player);
        clusteredUserRegistry.delete(player);

        if (user != null) {
            clanUserService.disconnect(user);
        }
    }

    @Override
    public void saveAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = userService.forcedSave(player);

            clusteredUserRegistry.delete(player);

            if (user != null) {
                clanUserService.forceDisconnect(user);
            }
        }
    }
}
