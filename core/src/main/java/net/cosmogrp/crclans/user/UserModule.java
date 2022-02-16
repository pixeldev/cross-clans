package net.cosmogrp.crclans.user;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.dist.LocalModelService;
import net.cosmogrp.storage.model.meta.ModelMeta;
import net.cosmogrp.storage.mongo.MongoModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class UserModule extends AbstractModule {

    @Override
    public void configure() {
        bind(UserService.class).to(SimpleUserService.class).singleton();
        bind(ClanUserService.class).to(SimpleClanUserService.class).singleton();
        bind(ClusteredUserRegistry.class).to(SimpleClusteredUserRegistry.class).singleton();
    }

    @Provides @Singleton
    public CachedRemoteModelService<User> createService(
            Executor executor, FileConfiguration configuration,
            MongoDatabase mongoDatabase
    ) {
        ModelMeta<User> modelMeta = new ModelMeta<>(User.class)
                .addProperty("collection", configuration.getString("server-group") + ":users");

        return new MongoModelService<>(
                executor, modelMeta,
                new LocalModelService<>(),
                mongoDatabase,
                User::fromDocument
        );
    }

    @Provides @Singleton
    public RedisModelService<User> createRedisService(
            Executor executor, Gson gson,
            RedisCache redisCache
    ) {
        ModelMeta<User> modelMeta = new ModelMeta<>(User.class)
                .addProperty("redis-table", "users")
                .addProperty("redis-expire", 300);

        return new RedisModelService<>(
                executor, modelMeta,
                gson, redisCache
        );
    }

}
