package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.domain.Domain;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

    public static <T extends Model & DocumentCodec & Domain>
    List<String> getSuggestions(
            UserService userService,
            CommandContext context,
            ClanService<T> service,
            ArgumentStack stack
    ) {
        String clanTag = PartHelper.getClanTag(userService, context);

        if (clanTag == null) {
            return Collections.emptyList();
        }

        T data = service.getData(clanTag);

        if (data == null) {
            return Collections.emptyList();
        }

        String next = !stack.hasNext() ? "" :
                stack.next().toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();

        for (String allyTag : data.getAll()) {
            if (allyTag.toLowerCase(Locale.ROOT).startsWith(next)) {
                suggestions.add(allyTag);
            }
        }

        return suggestions;
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
