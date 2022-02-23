package net.cosmogrp.crclans.clan;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.inject.key.TypeReference;
import net.cosmogrp.crclans.clan.disband.ClanDisbandChannelListener;
import net.cosmogrp.crclans.clan.disband.ClanDisbandMessage;
import net.cosmogrp.crclans.clan.home.ClanHomeData;
import net.cosmogrp.crclans.clan.home.ClanHomeService;
import net.cosmogrp.crclans.clan.member.ClanKickChannelListener;
import net.cosmogrp.crclans.clan.member.ClanKickMessage;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentData;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
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
                )
        );

        multibind(new TypeReference<ClanService<?>>() {})
                .asMap(String.class)
                .bind(ClanDataService.KEY)
                .to(ClanDataService.class)
                .bind(ClanRecruitmentService.KEY)
                .to(ClanRecruitmentService.class)
                .bind(ClanHomeService.KEY)
                .to(ClanHomeService.class)
                .bind(ClanMemberService.KEY)
                .to(ClanMemberService.class);
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
