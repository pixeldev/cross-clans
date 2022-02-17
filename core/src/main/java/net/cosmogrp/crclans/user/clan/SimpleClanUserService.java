package net.cosmogrp.crclans.user.clan;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.storage.AsyncModelService;
import net.cosmogrp.storage.redis.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;
import java.util.function.Consumer;

public class SimpleClanUserService
        implements ClanUserService {

    @Inject private ClanService clanService;
    @Inject private UserService userService;
    @Inject private MessageHandler messageHandler;
    @Inject private GlobalNotifier globalNotifier;
    @Inject private AsyncModelService<Clan> modelService;
    @Inject private ClusteredUserRegistry clusteredUserRegistry;
    @Inject private LogHandler logHandler;
    @Inject private Channel<ClanKickMessage> kickChannel;

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
    public void kickMember(
            Player player, User user,
            Clan clan, ClanMember target
    ) {
        ClanMember clanMember = clan.getMember(user.getPlayerId());

        if (clanMember == null) {
            // this should never happen
            return;
        }

        if (!clanMember.isModerator()) {
            messageHandler.send(player, "clan.not-mod");
            return;
        }

        if (target.isModerator()) {
            messageHandler.send(player, "clan.kick-mod");
            return;
        }

        UUID targetId = target.getPlayerId();
        String targetName = target.getPlayerName();

        clan.removeMember(targetId);
        messageHandler.sendReplacing(
                player, "clan.kick-success-sender",
                "%target%", targetName
        );

        globalNotifier.notify(
                clan.getOnlineMembers(), "clan.kick-success-members",
                "%sender%", player.getName(),
                "%target%", targetName
        );

        ClusteredUser clusteredTarget =
                clusteredUserRegistry.find(targetName);

        if (clusteredTarget != null) {
            String clanTag = clan.getId();
            if (notifyKick(targetId, clanTag)) {
                return;
            }

            kickChannel.sendMessage(
                    new ClanKickMessage(targetId, clanTag),
                    clusteredTarget.getServerData().getRedisServer()
            );
        }

        clanService.saveClan(player, clan);
    }

    @Override
    public void promoteMember(
            Player player, User user,
            Clan clan, ClanMember target
    ) {
        if (!clan.isOwner(player)) {
            messageHandler.send(player, "clan.not-owner");
            return;
        }

        if (target.isModerator()) {
            messageHandler.send(player, "clan.already-mod");
            return;
        }

        target.setModerator(true);
        messageHandler.sendReplacing(
                player, "clan.promote-success-sender",
                "%target%", target.getPlayerName()
        );

        globalNotifier.notify(
                clan.getOnlineMembers(), "clan.promote-success-members",
                "%sender%", player.getName(),
                "%target%", target.getPlayerName()
        );

        globalNotifier.singleNotify(
                target.getPlayerId(),
                "clan.promote-success-target",
                "%sender%", player.getName()
        );

        clanService.saveClan(player, clan);
    }

    @Override
    public boolean notifyKick(UUID targetId, String clanId) {
        Player player = Bukkit.getPlayer(targetId);

        if (player == null) {
            return false;
        }

        User user = userService.getUser(player);

        if (user == null) {
            return false;
        }

        user.setClan(null);
        messageHandler.sendReplacing(
                player, "clan.kick-success-target",
                "%tag%", clanId
        );

        return true;
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

                                messageHandler.send(player, "clan.disband-failed");
                                return;
                            }

                            // just remove clan from the owner
                            // we will wait to members get connected to the server
                            user.setClan(null);

                            // TODO: remove clan from all online members
                            messageHandler.send(player, "clan.disband-success");
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
