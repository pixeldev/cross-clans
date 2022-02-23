package net.cosmogrp.crclans.clan.service;

import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.ClanData;
import net.cosmogrp.crclans.clan.SimpleClanDataService;
import net.cosmogrp.crclans.clan.home.ClanHomeData;
import net.cosmogrp.crclans.clan.home.SimpleClanHomeService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.SimpleClanMemberService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentData;
import net.cosmogrp.crclans.clan.recruitment.SimpleClanRecruitmentService;
import net.cosmogrp.crclans.loader.Loader;

import javax.inject.Inject;

public class ClanServicesLoader implements Loader {

    @Inject private CrClansPlugin plugin;

    @Override
    public void load() {
        plugin.registerService(ClanData.class, SimpleClanDataService.class);
        plugin.registerService(ClanMemberData.class, SimpleClanMemberService.class);
        plugin.registerService(ClanRecruitmentData.class, SimpleClanRecruitmentService.class);
        plugin.registerService(ClanHomeData.class, SimpleClanHomeService.class);
    }
}
