package net.cosmogrp.crclans.clan.recruitment;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.AbstractClanService;
import net.cosmogrp.crclans.clan.ClanService;
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
import java.util.Map;
import java.util.UUID;

public class SimpleClanRecruitmentService
        extends AbstractClanService<ClanRecruitmentData>
        implements ClanRecruitmentService {

    @Inject private GlobalNotifier globalNotifier;
    @Inject private MessageHandler messageHandler;
    @Inject private FileConfiguration configuration;

    private final ClanMemberService memberService;

    @Inject
    public SimpleClanRecruitmentService(Map<String, ClanService<?>> services) {
        this.memberService = (ClanMemberService)
                services.get(ClanMemberService.KEY);
    }

    @Override
    public void sendRecruitment(
            Player sender, User user,
            ClusteredUser target
    ) {
        String tag = user.getClanTag();
        ClanMemberData memberData =
                memberService.getData(sender, tag);

        if (memberData == null) {
            return;
        }

        ClanMember clanMember = memberData.getMember(sender.getUniqueId());

        if (clanMember == null) {
            // this should never happen
            return;
        }

        if (!clanMember.isModerator()) {
            messageHandler.send(sender, "clan.not-mod");
            return;
        }

        UUID targetId = target.getPlayerId();

        if (memberData.isMember(targetId)) {
            messageHandler.send(sender, "clan.already-member");
            return;
        }

        ClanRecruitmentData recruitmentData = getData(tag);

        if (recruitmentData == null) {
            recruitmentData = ClanRecruitmentData.create(tag);

            int time = configuration.getInt("clans.invite-expiry");
            RecruitmentRequest request = RecruitmentRequest
                    .create(target, time);

            recruitmentData.addRequest(request);
            messageHandler.sendReplacing(
                    sender, "clan.invited-sender",
                    "%target%", target.asPlayer().getName()
            );

            globalNotifier.singleNotifyIn(
                    target.getPlayerId(), "minimessage",
                    "clan.invited-target",
                    "%tag%", memberData.getId(),
                    "%time%", time
            );
        } else {
            RecruitmentRequest request =
                    recruitmentData.getRequest(targetId);

            if (request != null) {
                if (request.isExpired()) {
                    recruitmentData.removeRequest(request);
                } else {
                    messageHandler.send(sender, "clan.already-invited");
                }
            }
        }

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

        globalNotifier.singleNotify(
                memberData.getOwner().getPlayerId(),
                "clan.invite-accepted-sender",
                "%target%", sender.getName()
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

        ClanMember owner = memberData.getOwner();

        if (owner.isOnline()) {
            globalNotifier.singleNotify(
                    owner.getPlayerId(),
                    "clan.invite-deny-sender",
                    "%target%", sender.getName()
            );
        }

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
            return null;
        }

        return request;
    }

}
