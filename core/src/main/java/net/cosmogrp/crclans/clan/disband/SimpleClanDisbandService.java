package net.cosmogrp.crclans.clan.disband;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.home.ClanHomeService;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.storage.redis.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SimpleClanDisbandService
        implements ClanDisbandService {

    @Inject private MessageHandler messageHandler;
    @Inject private GlobalNotifier globalNotifier;
    @Inject private LogHandler logHandler;

    @Inject private ClanUserService clanUserService;
    @Inject private UserService userService;

    @Inject private Channel<ClanDisbandMessage> disbandChannel;

    private final ClanMemberService memberService;
    private final Map<String, ClanService<?>> services;

    @Inject
    public SimpleClanDisbandService(Map<String, ClanService<?>> services) {
        this.services = services;
        this.memberService = (ClanMemberService)
                services.get(ClanMemberService.KEY);
    }

    @Override
    public void disbandClan(Player player, User user) {
        memberService.computeAsOwner(
                player, user,
                memberData -> modelService
                        .delete(memberData)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                logHandler.reportError(
                                        "Failed to delete clan '%s'", error,
                                        memberData.getId()
                                );

                                messageHandler.send(player, "clan.disband-failed");
                                return;
                            }

                            // just remove clan from the owner
                            // we will wait to members get connected to the server
                            user.setClan(null);
                            memberData.removeMember(user.getPlayerId());

                            messageHandler.send(player, "clan.disband-success");

                            Set<UUID> onlineMembers = memberData.getOnlineMembers();

                            if (notifyDisband(onlineMembers)) {
                                return;
                            }

                            disbandChannel.sendMessage(new ClanDisbandMessage(onlineMembers));
                        }));
    }

    @Override
    public boolean notifyDisband(Set<UUID> onlineMembers) {
        boolean success = true;

        for (UUID uuid : onlineMembers) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                success = false;
                continue;
            }

            User user = userService.getUser(uuid);

            if (user == null) {
                success = false;
                continue;
            }

            user.setClan(null);
            messageHandler.send(player, "clan.disband-members");
        }

        return success;
    }

    @Override
    public void leaveClan(Player player, User user) {
        Clan clan = clanUserService.getClan(player, user);

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

        clanService.save(player, clan);
    }

}
