package net.cosmogrp.crclans.clan;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.clan.chat.ClanChatService;
import net.cosmogrp.crclans.clan.chat.SimpleClanChatService;
import net.cosmogrp.crclans.clan.disband.ClanDisbandChannelListener;
import net.cosmogrp.crclans.clan.disband.ClanDisbandMessage;
import net.cosmogrp.crclans.clan.disband.ClanDisbandService;
import net.cosmogrp.crclans.clan.disband.SimpleClanDisbandService;
import net.cosmogrp.crclans.clan.home.ClanHomeService;
import net.cosmogrp.crclans.clan.home.SimpleClanHomeService;
import net.cosmogrp.crclans.clan.member.ClanKickChannelListener;
import net.cosmogrp.crclans.clan.member.ClanKickMessage;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.clan.member.SimpleClanMemberService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.clan.recruitment.SimpleClanRecruitmentService;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.model.meta.ModelMeta;
import net.cosmogrp.storage.mongo.MongoModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.connection.Redis;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class ClanModule extends AbstractModule {

    @Override
    public void configure() {
        bind(ClanRecruitmentService.class).to(SimpleClanRecruitmentService.class).singleton();
        bind(ClanHomeService.class).to(SimpleClanHomeService.class).singleton();
        bind(ClanDisbandService.class).to(SimpleClanDisbandService.class).singleton();
        bind(ClanMemberService.class).to(SimpleClanMemberService.class).singleton();
        bind(ClanChatService.class).to(SimpleClanChatService.class).singleton();
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

    @Provides @Singleton
    public CachedRemoteModelService<ClanData> createService(
            Executor executor, FileConfiguration configuration,
            MongoDatabase mongoDatabase, Gson gson,
            RedisCache redisCache
    ) {
        ModelMeta<ClanData> modelMeta = new ModelMeta<>(ClanData.class)
                .addProperty("redis-table", "clans-data")
                .addProperty("collection", configuration.getString("server-group") + ":clans-data");

        return new MongoModelService<>(
                executor, modelMeta,
                new RedisModelService<>(executor, modelMeta, gson, redisCache),
                mongoDatabase,
                ClanData::fromDocument
        );
    }

}
