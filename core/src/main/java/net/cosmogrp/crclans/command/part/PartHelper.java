package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class PartHelper {

    private PartHelper() {
    }

    public static @Nullable User getUser(
            UserService userService,
            CommandContext context
    ) {
        CommandSender commandSender = context.getObject(
                CommandSender.class,
                BukkitCommandManager.SENDER_NAMESPACE
        );

        if (!(commandSender instanceof Player player)) {
            return null;
        }

        return userService.getUser(player.getUniqueId());
    }

    public static @Nullable String getClanTag(
            UserService userService,
            CommandContext context
    ) {
        User user = getUser(userService, context);
        if (user == null) {
            return null;
        }
        return user.getClanTag();
    }

}
