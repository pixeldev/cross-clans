package net.cosmogrp.crclans.clan.channel;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

abstract class AbstractClanChannel implements ClanChannel {

    @Inject private FileConfiguration configuration;
    @Inject private MessageHandler messageHandler;
    @Inject protected GlobalNotifier globalNotifier;

    @Override
    public boolean sendMessage(
            String clanTag, Player sender,
            String message
    ) {
        String messageFormat =configuration.getString(
                "clans.channels.message-format",
                "&f%player_name% &a: &f%message%"
        );

        String messageFormatted = PlaceholderAPI
                .setPlaceholders(sender, messageFormat)
                .replace("%message%", message);

        Set<UUID> targets = makeTargets(clanTag);

        if (targets == null) {
            messageHandler.send(sender, "clans.error-sending-message");
            return false;
        }

        globalNotifier.notify(
                targets,
                "channels.prefix",
                "%channel%", getId(),
                "%message%", messageFormatted
        );
        return true;
    }

    protected abstract @Nullable Set<UUID> makeTargets(String clanTag);
}
