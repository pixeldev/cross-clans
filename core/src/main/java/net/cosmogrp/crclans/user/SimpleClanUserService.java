package net.cosmogrp.crclans.user;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.function.Consumer;

public class SimpleClanUserService
        implements ClanUserService {

    @Inject private ClanService clanService;
    @Inject private MessageHandler messageHandler;

    @Override
    public @Nullable Clan getClan(Player player, User user) {
        if (!user.hasClan()) {
            messageHandler.send(player, "clan.not-in-clan");
            return null;
        }

        Clan clan = clanService.getClan(user.getClanTag());

        if (clan == null) {
            messageHandler.send(player, "clan.error-finding-clan");
        }

        return clan;
    }

    @Override
    public void executeAsOwner(
            Player player, User user,
            Consumer<Clan> consumer
    ) {
        Clan clan = getClan(player, user);

        if (clan == null) {
            return;
        }

        if (!clan.isOwner(player)) {
            messageHandler.send(player, "clan.not-owner");
            return;
        }

        consumer.accept(clan);
    }
}
