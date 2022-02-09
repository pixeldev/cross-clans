package net.cosmogrp.crclans.user;

import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.storage.AsyncModelService;
import net.cosmogrp.storage.dist.LocalModelService;
import net.cosmogrp.storage.model.meta.ModelMeta;
import net.cosmogrp.storage.mongo.MongoModelService;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class UserModule extends AbstractModule {

    @Provides @Singleton
    public AsyncModelService<User> createService(
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

}
