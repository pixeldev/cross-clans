package net.cosmogrp.crclans.user;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.storage.AsyncModelService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.function.Consumer;

public class SimpleClanUserService
        implements ClanUserService {

    @Inject private ClanService clanService;
    @Inject private MessageHandler messageHandler;
    @Inject private GlobalNotifier globalNotifier;
    @Inject private AsyncModelService<Clan> modelService;
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
    public void disbandClan(Player player, User user) {
        executeAsOwner(
                player, user,
                clan -> modelService
                        .delete(clan)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                logHandler.reportError(
                                        "Failed to delete clan '%s'", error,
                                        clan.getId()
                                );

                                messageHandler.send(player, "clan.delete-failed");
                                return;
                            }

                            // just remove clan from the owner
                            // we will wait to members get connected to the server
                            user.setClan(null);

                            // TODO: remove clan from all online members
                            messageHandler.send(player, "clan.delete-success");
                        }));
    }

    @Override
    public void leaveClan(Player player, User user) {
        Clan clan = getClan(player, user);

        if (clan == null) {
            return;
        }

        if (clan.isOwner(player)) {
            messageHandler.send(player, "clan.cannot-leave-own-clan");
            return;
        }

        ClanMember clanMember = clan.removeMember(user.getPlayerId());

        if (clanMember == null) {
            messageHandler.send(player, "clan.error-leaving-clan");
            return;
        }

        user.setClan(null);

        messageHandler.sendReplacing(
                player, "clan.left-clan-sender",
                "%tag%", clan.getId()
        );

        globalNotifier.notify(
                clan.getOnlineMembers(),
                clanMember.isModerator() ?
                        "clan.moderator-left-clan-members"
                        : "clan.left-clan-members",
                "%target%", player.getName()
        );

        clanService.saveClan(player, clan);
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
