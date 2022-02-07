package net.cosmogrp.crclans.notifier;

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
        notify0(targets, path, parameters);
    }

    protected boolean notify0(Set<UUID> targets, String path, Object... parameters) {
        if (targets == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                messageHandler.sendReplacing(player, path, parameters);
            }

            return false;
        } else {
            boolean delivered = true;
            for (UUID target : targets) {
                Player player = Bukkit.getPlayer(target);

                if (player == null) {
                    delivered = false;
                    continue;
                }

                messageHandler.sendReplacing(player, path, parameters);
            }
            return delivered;
        }
    }
}
