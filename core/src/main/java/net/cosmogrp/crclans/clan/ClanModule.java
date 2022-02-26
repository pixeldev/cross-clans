package net.cosmogrp.crclans.clan;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestService;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.clan.ally.SimpleClanAllyRequestService;
import net.cosmogrp.crclans.clan.ally.SimpleClanAllyService;
import net.cosmogrp.crclans.clan.channel.ClanChannelRegistry;
import net.cosmogrp.crclans.clan.channel.SimpleClanChannelRegistry;
import net.cosmogrp.crclans.clan.chat.ClanChatService;
import net.cosmogrp.crclans.clan.chat.SimpleClanChatService;
import net.cosmogrp.crclans.clan.disband.ClanDisbandChannelListener;
import net.cosmogrp.crclans.clan.disband.ClanDisbandMessage;
import net.cosmogrp.crclans.clan.disband.ClanDisbandService;
import net.cosmogrp.crclans.clan.disband.SimpleClanDisbandService;
import net.cosmogrp.crclans.clan.home.ClanHomeData;
import net.cosmogrp.crclans.clan.home.ClanHomeService;
import net.cosmogrp.crclans.clan.home.SimpleClanHomeService;
import net.cosmogrp.crclans.clan.inject.ClanServiceModule;
import net.cosmogrp.crclans.clan.member.ClanKickChannelListener;
import net.cosmogrp.crclans.clan.member.ClanKickMessage;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.clan.member.SimpleClanMemberService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentData;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.clan.recruitment.SimpleClanRecruitmentService;
import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.connection.Redis;

import javax.inject.Singleton;

public class ClanModule extends AbstractModule {

    @Override
    public void configure() {
        install(
                new ClanServiceModule<>(
                        ClanDataService.KEY,
                        ClanData::fromDocument, ClanData.class
                ),
                new ClanServiceModule<>(
                        ClanRecruitmentService.KEY,
                        ClanRecruitmentData::fromDocument, ClanRecruitmentData.class
                ),
                new ClanServiceModule<>(
                        ClanHomeService.KEY,
                        ClanHomeData::fromDocument, ClanHomeData.class
                ),
                new ClanServiceModule<>(
                        ClanMemberService.KEY,
                        ClanMemberData::fromDocument, ClanMemberData.class
                ),
                new ClanServiceModule<>(
                        ClanAllyService.KEY,
                        ClanAllyData::fromDocument, ClanAllyData.class
                ),
                new ClanServiceModule<>(
                        ClanAllyRequestService.KEY,
                        ClanAllyRequestData::fromDocument, ClanAllyRequestData.class
                )
        );

        bind(ClanChannelRegistry.class).to(SimpleClanChannelRegistry.class).singleton();

        bind(ClanChatService.class).to(SimpleClanChatService.class).singleton();
        bind(ClanAllyService.class).to(SimpleClanAllyService.class).singleton();
        bind(ClanAllyRequestService.class).to(SimpleClanAllyRequestService.class).singleton();
        bind(ClanDisbandService.class).to(SimpleClanDisbandService.class).singleton();
        bind(ClanRecruitmentService.class).to(SimpleClanRecruitmentService.class).singleton();
        bind(ClanHomeService.class).to(SimpleClanHomeService.class).singleton();
        bind(ClanDataService.class).to(SimpleClanDataService.class).singleton();
        bind(ClanMemberService.class).to(SimpleClanMemberService.class).singleton();
    }

    @Provides
    @Singleton
    public Channel<ClanKickMessage> createKickChannel(
            Redis redis, ClanKickChannelListener listener
    ) {
        return redis.getMessenger()
                .getChannel("kick", ClanKickMessage.class)
                .addListener(listener);
    }

    @Provides
    @Singleton
    public Channel<ClanDisbandMessage> createDisbandChannel(
            Redis redis, ClanDisbandChannelListener listener
    ) {
        return redis.getMessenger()
                .getChannel("disband", ClanDisbandMessage.class)
                .addListener(listener);
    }

}
