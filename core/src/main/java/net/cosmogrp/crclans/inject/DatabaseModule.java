package net.cosmogrp.crclans.inject;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;

public class DatabaseModule extends AbstractModule {

    @Provides @Singleton
    public MongoDatabase createDatabase(FileConfiguration configuration) {
        ConfigurationSection databaseSection =
                configuration.getConfigurationSection("database");

        if (databaseSection == null) {
            throw new IllegalArgumentException("No database section found in configuration");
        }

        String connectionUri = databaseSection.getString(
                "connection",
                "mongodb://127.0.0.1:27017"
        );

        return MongoClients.create(new ConnectionString(connectionUri))
                .getDatabase(databaseSection.getString("name", "clans"));
    }

}
