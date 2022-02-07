package net.cosmogrp.crclans.messenger;

import me.yushust.message.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

public class LocalNotifier implements Notifier {

    @Inject protected MessageHandler messageHandler;

    @Override
    public void notify(Set<UUID> targets, String path, Object... parameters) {
        if (targets == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                messageHandler.send(player, path, parameters);
            }
        } else {
            for (UUID target : targets) {
                Player player = Bukkit.getPlayer(target);
                if (player != null) {
                    messageHandler.send(player, path, parameters);
                }
            }
        }
    }
}
