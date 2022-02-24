package net.cosmogrp.crclans.clan.service;

import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.ClanData;
import net.cosmogrp.crclans.clan.ClanDataService;
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
    }
}
