package net.cosmogrp.crclans.clan.recruitment;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.service.AbstractClanService;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleClanRecruitmentService
        extends AbstractClanService<ClanRecruitmentData>
        implements ClanRecruitmentService {

    @Inject private GlobalNotifier globalNotifier;
    @Inject private MessageHandler messageHandler;
    @Inject private FileConfiguration configuration;

    @Inject private ClanMemberService memberService;

    public SimpleClanRecruitmentService() {
        super(ClanRecruitmentData::create);
    }

    @Override
    public void sendRecruitment(
            Player sender, User user,
            ClusteredUser target
    ) {
        String tag = memberService.getClanTag(sender, user);

        if (tag == null) {
            return;
        }

        ClanMemberData memberData =
                memberService.getData(sender, tag);

        if (memberData == null ||
                !memberService.checkModerator(sender, memberData)) {
            return;
        }

        UUID targetId = target.getPlayerId();

        if (memberData.isMember(targetId)) {
            messageHandler.send(sender, "clan.already-member");
            return;
        }

        ClanRecruitmentData recruitmentData = getData(sender, tag);

        if (recruitmentData == null) {
            return;
        }

        RecruitmentRequest request =
                recruitmentData.getRequest(targetId);

        if (request != null) {
            if (request.isExpired()) {
                recruitmentData.removeRequest(request);
            } else {
                messageHandler.send(sender, "clan.already-invited");
                return;
            }
        }

        int time = configuration.getInt("clans.invite-expiry");
        request = RecruitmentRequest
                .create(target, time);

        recruitmentData.addRequest(request);

        globalNotifier.notify(
                memberData.getOnlineIdMembers(),
                "clan.invited-members",
                "%target%", target.asPlayer().getName()
        );

        globalNotifier.singleNotifyIn(
                target.getPlayerId(), "minimessage",
                "clan.invited-target",
                "%tag%", memberData.getId(),
                "%time%", time
        );

        save(sender, recruitmentData);
    }

    @Override
    public void acceptRecruitment(
            Player sender, User user,
            ClanRecruitmentData data
    ) {
        if (user.hasClan()) {
            messageHandler.send(sender, "clan.already-in-clan");
            return;
        }

        RecruitmentRequest request = checkRequest(data, sender);

        if (request == null) {
            return;
        }

        String tag = data.getId();
        ClanMemberData memberData = memberService
                .getData(sender, tag);

        if (memberData == null) {
            return;
        }

        messageHandler.sendReplacing(
                sender, "clan.invite-accepted-target",
                "%tag%", tag
        );

        globalNotifier.notify(
                memberData.getOnlineIdMembers(),
                "clan.invite-accepted-members",
                "%target%", sender.getName()
        );

        user.setClan(tag);
        memberData.addMember(sender);
        data.removeRequest(request);

        save(sender, data);
        memberService.save(sender, memberData);
    }

    @Override
    public void denyRecruitment(
            Player sender,
            ClanRecruitmentData data
    ) {
        RecruitmentRequest request = checkRequest(data, sender);

        if (request == null) {
            return;
        }

        String tag = data.getId();
        ClanMemberData memberData = memberService
                .getData(sender, tag);

        if (memberData == null) {
            return;
        }

        data.removeRequest(request);
        messageHandler.sendReplacing(
                sender, "clan.invite-deny-target",
                "%tag%", tag
        );

        globalNotifier.notify(
                memberData.getOnlineIdMembers(),
                "clan.invite-deny-members",
                "%target%", sender.getName()
        );

        save(sender, data);
    }

    private @Nullable RecruitmentRequest checkRequest(
            ClanRecruitmentData data,
            Player sender
    ) {
        RecruitmentRequest request = data.getRequest(sender.getUniqueId());

        if (request == null) {
            messageHandler.send(sender, "clan.no-invite");
            return null;
        }

        if (request.isExpired()) {
            data.removeRequest(request);
            messageHandler.send(sender, "clan.invite-expired");
            save(sender, data);
            return null;
        }

        return request;
    }

}
