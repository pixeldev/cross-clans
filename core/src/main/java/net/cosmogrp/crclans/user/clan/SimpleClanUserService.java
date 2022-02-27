package net.cosmogrp.crclans.user.clan;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class SimpleClanUserService
        implements ClanUserService {

    @Inject private ClanMemberService memberService;

    @Inject private MessageHandler messageHandler;
    @Inject private LogHandler logHandler;

    @Override
    public void connect(Player player, User user) {
        String clanTag = user.getClanTag();

        if (clanTag == null) {
            return;
        }

        ClanMemberData memberData = memberService.getData(clanTag);

        // in most cases this happens if clan has
        // been disbanded and player wasn't
        // online when it happened
        if (memberData == null) {
            user.setClan(null);
            messageHandler.send(player, "clan.disband-offline-notify");
            return;
        }

        ClanMember member = memberData.getMember(user.getPlayerId());

        // in most cases this happens if player
        // has been kicked from clan
        if (member == null) {
            user.setClan(null);
            messageHandler.send(player, "clan.kick-offline-notify");
            return;
        }

        member.setOnline(true);
        memberService.save(player, memberData);
    }

    @Override
    public void disconnect(User user) {
        ClanMemberData memberData = internalDisconnect(user);
        if (memberData != null) {
            memberService.save(memberData);
        }
    }

    @Override
    public void forceDisconnect(User user) {
        ClanMemberData memberData = internalDisconnect(user);
        if (memberData != null) {
            memberService.saveSync(memberData);
        }
    }

    private @Nullable ClanMemberData internalDisconnect(User user) {
        String clanTag = user.getClanTag();

        if (clanTag == null) {
            return null;
        }

        ClanMemberData memberData = memberService.getData(clanTag);

        if (memberData == null) {
            return null;
        }

        ClanMember member = memberData.getMember(user.getPlayerId());

        if (member == null) {
            // this should never happen
            logHandler.reportError(
                    "Failed to find member '%s' in clan '%s'",
                    user.getPlayerId().toString(), clanTag
            );
            return null;
        }

        member.setOnline(false);
        return memberData;
    }
}
