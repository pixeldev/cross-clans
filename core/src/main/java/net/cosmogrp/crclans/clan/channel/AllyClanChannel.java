package net.cosmogrp.crclans.clan.channel;

import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AllyClanChannel extends AbstractClanChannel {

    public static String ID = "allies";

    @Inject private ClanAllyService allyService;
    @Inject private ClanMemberService memberService;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected @Nullable Set<UUID> makeTargets(String clanTag) {
        ClanAllyData allyData = allyService.getData(clanTag);

        if (allyData == null) {
            return null;
        }

        ClanMemberData currentMemberData = memberService.getData(clanTag);

        if (currentMemberData == null) {
            return null;
        }

        Set<UUID> targets = new HashSet<>(
                currentMemberData.getOnlineIdMembers()
        );

        Collection<String> allies = allyData.getAll();

        for (String allyTag : allies) {
            ClanMemberData memberData = memberService.getData(allyTag);

            if (memberData == null) {
                continue;
            }

            targets.addAll(memberData.getOnlineIdMembers());
        }

        return targets;
    }
}
