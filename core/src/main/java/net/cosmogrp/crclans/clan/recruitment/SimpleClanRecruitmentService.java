package net.cosmogrp.crclans.clan.recruitment;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleClanRecruitmentService
        implements ClanRecruitmentService {

    @Inject private GlobalNotifier globalNotifier;
    @Inject private ClanUserService clanUserService;
    @Inject private MessageHandler messageHandler;
    @Inject private FileConfiguration configuration;
    @Inject private ClanService clanService;

    @Override
    public void sendRecruitment(
            Player sender, User user,
            ClusteredUser target
    ) {
        Clan clan = clanUserService.getClan(sender, user);

        if (clan == null) {
            return;
        }

        ClanMember clanMember = clan.getMember(sender.getUniqueId());

        if (clanMember == null) {
            // this should never happen
            return;
        }

        if (!clanMember.isModerator()) {
            messageHandler.send(sender, "clan.not-mod");
            return;
        }

        UUID targetId = target.getPlayerId();

        if (clan.isMember(targetId)) {
            messageHandler.send(sender, "clan.already-member");
            return;
        }

        RecruitmentRequest request =
                clan.getRequest(targetId);

        if (request != null) {
            if (request.isExpired()) {
                clan.removeRequest(request);
            } else {
                messageHandler.send(sender, "clan.already-invited");
                return;
            }
        }

        int time = configuration.getInt("clans.invite-expiry");
        request = RecruitmentRequest.create(
                target, time
        );

        clan.addRequest(request);
        messageHandler.sendReplacing(
                sender, "clan.invited-sender",
                "%target%", target.asPlayer().getName()
        );

        globalNotifier.singleNotifyIn(
                target.getPlayerId(), "minimessage",
                "clan.invited-target",
                "%tag%", clan.getId(),
                "%time%", time
        );

        clanService.saveClan(sender, clan);
    }

    @Override
    public void acceptRecruitment(
            Player sender, User user,
            Clan clan
    ) {
        if (user.hasClan()) {
            messageHandler.send(sender, "clan.already-in-clan");
            return;
        }

        RecruitmentRequest request = checkRequest(clan, sender);

        if (request == null) {
            return;
        }

        messageHandler.sendReplacing(
                sender, "clan.invite-accepted-target",
                "%tag%", clan.getId()
        );

        globalNotifier.singleNotify(
                clan.getOwner().getPlayerId(),
                "clan.invite-accepted-sender",
                "%target%", sender.getName()
        );

        globalNotifier.notify(
                clan.getOnlineMembers(),
                "clan.invite-accepted-members",
                "%target%", sender.getName()
        );

        user.setClan(clan);
        clan.addMember(sender);
        clan.removeRequest(request);

        clanService.saveClan(sender, clan);
    }

    @Override
    public void denyRecruitment(
            Player sender, Clan clan
    ) {
        RecruitmentRequest request = checkRequest(clan, sender);

        if (request == null) {
            return;
        }

        clan.removeRequest(request);
        messageHandler.sendReplacing(
                sender, "clan.invite-deny-target",
                "%tag%", clan.getId()
        );

        globalNotifier.singleNotify(
                clan.getOwner().getPlayerId(),
                "clan.invite-deny-sender",
                "%target%", sender.getName()
        );

        clanService.saveClan(sender, clan);
    }

    private @Nullable RecruitmentRequest checkRequest(
            Clan clan,
            Player sender
    ) {
        RecruitmentRequest request = clan.getRequest(sender.getUniqueId());

        if (request == null) {
            messageHandler.send(sender, "clan.no-invite");
            return null;
        }

        if (request.isExpired()) {
            clan.removeRequest(request);
            messageHandler.send(sender, "clan.invite-expired");
            return null;
        }

        return request;
    }

}
