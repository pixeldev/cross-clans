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

    @Inject private ClanAllyService allyService;
    @Inject private ClanMemberService memberService;

    @Override
    public String getId() {
        return "ally";
    }

    @Override
    protected @Nullable Set<UUID> makeTargets(String clanTag) {
        ClanAllyData allyData = allyService.getData(clanTag);

        if (allyData == null) {
            return null;
        }

        Set<UUID> targets = new HashSet<>();
        Collection<String> allies = allyData.getAllies();

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
