package net.cosmogrp.crclans.clan;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.model.meta.ModelMeta;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.MongoModelParser;
import net.cosmogrp.storage.mongo.MongoModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class ClanServiceModule<T extends Model & DocumentCodec>
        extends AbstractModule {

    private final String serviceName;
    private final MongoModelParser<T> mongoModelParser;
    private final Class<T> modelClass;

    public ClanServiceModule(
            String serviceName,
            MongoModelParser<T> mongoModelParser,
            Class<T> modelClass
    ) {
        this.serviceName = serviceName;
        this.mongoModelParser = mongoModelParser;
        this.modelClass = modelClass;
    }

    @Provides @Singleton
    public CachedRemoteModelService<T> createService(
            Executor executor, FileConfiguration configuration,
            MongoDatabase mongoDatabase, Gson gson,
            RedisCache redisCache
    ) {
        ModelMeta<T> modelMeta = new ModelMeta<>(modelClass)
                .addProperty("redis-table", serviceName)
                .addProperty(
                        "collection",
                        configuration.getString("server-group")
                                + ":" + serviceName
                );

        return new MongoModelService<>(
                executor, modelMeta,
                new RedisModelService<>(executor, modelMeta, gson, redisCache),
                mongoDatabase, mongoModelParser
        );
    }

}
