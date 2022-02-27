package net.cosmogrp.crclans.clan.channel;

import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

public class MemberClanChannel extends AbstractClanChannel {

    public static String ID = "members";

    @Inject private ClanMemberService memberService;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected Set<UUID> makeTargets(String clanTag) {
        ClanMemberData memberData = memberService.getData(clanTag);

        if (memberData == null) {
            return null;
        }

        return memberData.getOnlineIdMembers();
    }
}
