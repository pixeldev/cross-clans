package net.cosmogrp.crclans.clan.service;

import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.data.ClanData;
import net.cosmogrp.crclans.clan.data.ClanDataService;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestService;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.clan.enemy.ClanEnemyData;
import net.cosmogrp.crclans.clan.enemy.ClanEnemyService;
import net.cosmogrp.crclans.clan.home.ClanHomeData;
import net.cosmogrp.crclans.clan.home.ClanHomeService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentData;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.loader.Loader;

import javax.inject.Inject;

public class ClanServicesLoader implements Loader {

    @Inject private CrClansPlugin plugin;

    @Override
    public void load() {
        plugin.registerService(ClanData.class, ClanDataService.class);
        plugin.registerService(ClanMemberData.class, ClanMemberService.class);
        plugin.registerService(ClanRecruitmentData.class, ClanRecruitmentService.class);
        plugin.registerService(ClanHomeData.class, ClanHomeService.class);
        plugin.registerService(ClanAllyRequestData.class, ClanAllyRequestService.class);
        plugin.registerService(ClanAllyData.class, ClanAllyService.class);
        plugin.registerService(ClanEnemyData.class, ClanEnemyService.class);
    }
}
