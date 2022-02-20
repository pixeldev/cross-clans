package net.cosmogrp.crclans.clan;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.clan.disband.ClanDisbandChannelListener;
import net.cosmogrp.crclans.clan.disband.ClanDisbandMessage;
import net.cosmogrp.crclans.clan.mod.ClanKickChannelListener;
import net.cosmogrp.crclans.clan.mod.ClanKickMessage;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.clan.recruitment.SimpleClanRecruitmentService;
import net.cosmogrp.storage.AsyncModelService;
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
        bind(ClanService.class).to(SimpleClanService.class).singleton();
        bind(ClanRecruitmentService.class).to(SimpleClanRecruitmentService.class).singleton();
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
    public AsyncModelService<Clan> createService(
            Executor executor, FileConfiguration configuration,
            MongoDatabase mongoDatabase, Gson gson,
            RedisCache redisCache
    ) {
        ModelMeta<Clan> modelMeta = new ModelMeta<>(Clan.class)
                .addProperty("redis-table", "clans")
                .addProperty("collection", configuration.getString("server-group") + ":clans");

        return new MongoModelService<>(
                executor, modelMeta,
                new RedisModelService<>(executor, modelMeta, gson, redisCache),
                mongoDatabase,
                Clan::fromDocument
        );
    }

}
