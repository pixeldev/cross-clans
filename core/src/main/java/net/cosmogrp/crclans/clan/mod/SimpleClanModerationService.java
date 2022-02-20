package net.cosmogrp.crclans.clan.mod;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.storage.redis.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleClanModerationService
        implements ClanModerationService {

    @Inject private MessageHandler messageHandler;
    @Inject private GlobalNotifier globalNotifier;
    @Inject private ClusteredUserRegistry clusteredUserRegistry;

    @Inject private ClanService clanService;
    @Inject private UserService userService;

    @Inject private Channel<ClanKickMessage> kickChannel;

    @Override
    public void kickMember(Player player, User user, Clan clan, ClanMember target) {
        ClanMember clanMember = clan.getMember(user.getPlayerId());

        if (clanMember == null) {
            // this should never happen
            return;
        }

        if (!clanMember.isModerator()) {
            messageHandler.send(player, "clan.not-mod");
            return;
        }

        if (target.isModerator() && !clan.isOwner(player)) {
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

        clanService.saveClan(player, clan);

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
    }

    @Override
    public void promoteMember(Player player, User user, Clan clan, ClanMember target) {
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
    public void demoteMember(Player player, User user, Clan clan, ClanMember target) {
        if (!clan.isOwner(player)) {
            messageHandler.send(player, "clan.not-owner");
            return;
        }

        if (clan.isOwner(target.getPlayerId())) {
            messageHandler.send(player, "clan.cannot-demote-owner");
            return;
        }

        if (!target.isModerator()) {
            messageHandler.send(player, "clan.demote-not-mod");
            return;
        }

        target.setModerator(false);
        messageHandler.sendReplacing(
                player, "clan.demote-success-sender",
                "%target%", target.getPlayerName()
        );

        globalNotifier.notify(
                clan.getOnlineMembers(), "clan.demote-success-members",
                "%sender%", player.getName(),
                "%target%", target.getPlayerName()
        );

        globalNotifier.singleNotify(
                target.getPlayerId(),
                "clan.demote-success-target",
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
}
