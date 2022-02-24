package net.cosmogrp.crclans.clan.inject;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.key.TypeReference;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.model.meta.ModelMeta;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.MongoModelParser;
import net.cosmogrp.storage.mongo.MongoModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Inject;
import javax.inject.Provider;
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

    @Override
    public void configure() {
        bind(TypeReference.of(CachedRemoteModelService.class, modelClass))
                .toProvider(new Provider<>() {

                    @Inject private Executor executor;
                    @Inject private FileConfiguration configuration;
                    @Inject private MongoDatabase mongoDatabase;
                    @Inject private Gson gson;
                    @Inject private RedisCache redisCache;

                    @Override
                    public CachedRemoteModelService<T> get() {
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

                })
                .singleton();
    }

}
