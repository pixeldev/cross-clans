package net.cosmogrp.crclans.user.clan;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.function.Consumer;

public class SimpleClanUserService
        implements ClanUserService {

    @Inject private ClanService clanService;
    @Inject private MessageHandler messageHandler;
    @Inject private LogHandler logHandler;

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
    public void connect(Player player, User user) {
        String clanTag = user.getClanTag();

        if (clanTag == null) {
            return;
        }

        Clan clan = clanService.getClan(clanTag);

        // in most cases this happens if clan has
        // been disbanded and player wasn't
        // online when it happened
        if (clan == null) {
            user.setClan(null);
            messageHandler.send(player, "clan.disband-offline-notify");
            return;
        }

        ClanMember member = clan.getMember(user.getPlayerId());

        // in most cases this happens if player
        // has been kicked from clan
        if (member == null) {
            user.setClan(null);
            messageHandler.send(player, "clan.kick-offline-notify");
            return;
        }

        member.setOnline(true);
        clanService.saveClan(clan);
    }

    @Override
    public void disconnect(User user) {
        String clanTag = user.getClanTag();

        if (clanTag == null) {
            return;
        }

        Clan clan = clanService.getClan(clanTag);

        if (clan == null) {
            logHandler.reportError(
                    "Failed to find clan '%s' in user save",
                    clanTag
            );
            return;
        }

        ClanMember member = clan.getMember(user.getPlayerId());

        if (member == null) {
            logHandler.reportError(
                    "Failed to find member '%s' in clan '%s'",
                    user.getPlayerId().toString(), clanTag
            );
            return;
        }

        member.setOnline(false);
        clanService.saveClan(clan);
    }

    @Override
    public void executeAsOwner(
            Player player, User user,
            Consumer<Clan> consumer
    ) {
        executeAsOwner0(player, user, consumer);
    }

    @Override
    public void computeAsOwner(Player player, User user, Consumer<Clan> consumer) {
        Clan clan = executeAsOwner0(player, user, consumer);

        if (clan != null) {
            clanService.saveClan(player, clan);
        }
    }

    private @Nullable Clan executeAsOwner0(
            Player player, User user,
            Consumer<Clan> consumer
    ) {
        Clan clan = getClan(player, user);

        if (clan == null) {
            return null;
        }

        if (!clan.isOwner(player)) {
            messageHandler.send(player, "clan.not-owner");
            return null;
        }

        consumer.accept(clan);
        return clan;
    }
}
