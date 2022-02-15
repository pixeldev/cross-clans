package net.cosmogrp.crclans.notifier;

import me.yushust.message.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

public class LocalNotifier implements Notifier {

    @Inject protected MessageHandler messageHandler;

    @Override
    public void notify(
            @Nullable Set<UUID> targets,
            @Nullable String mode,
            String path, Object... parameters
    ) {
        notify0(targets, mode, path, parameters);
    }

    protected boolean notify0(
            @Nullable Set<UUID> targets,
            @Nullable String mode,
            String path, Object... parameters
    ) {
        if (targets == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (mode != null) {
                    messageHandler.sendReplacingIn(player, mode, path, parameters);
                } else {
                    messageHandler.sendReplacing(player, path, parameters);
                }
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

                if (mode != null) {
                    messageHandler.sendReplacingIn(player, mode, path, parameters);
                } else {
                    messageHandler.sendReplacing(player, path, parameters);
                }
            }
            return delivered;
        }
    }
}
