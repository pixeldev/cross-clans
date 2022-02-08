package net.cosmogrp.crclans.clan;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.inject.scope.Scopes;
import net.cosmogrp.storage.AsyncModelService;
import net.cosmogrp.storage.model.meta.ModelMeta;
import net.cosmogrp.storage.mongo.MongoModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class ClanModule extends AbstractModule {

    @Override
    public void configure() {
        bind(ClanService.class).to(SimpleClanService.class).in(Scopes.SINGLETON);
    }

    @Provides @Singleton
    public AsyncModelService<Clan> createService(
            Executor executor, FileConfiguration configuration,
            MongoDatabase mongoDatabase, Gson gson,
            RedisCache redisCache
    ) {
        ModelMeta<Clan> modelMeta = new ModelMeta<>(Clan.class)
                .addProperty("redis-table", "clans")
                .addProperty("collection", configuration.getString("server-group"));

        return new MongoModelService<>(
                executor, modelMeta,
                new RedisModelService<>(executor, modelMeta, gson, redisCache),
                mongoDatabase,
                new ClanModelParser()
        );
    }

}
