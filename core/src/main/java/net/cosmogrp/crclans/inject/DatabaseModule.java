package net.cosmogrp.crclans.inject;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.storage.sql.connection.SQLClient;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;

public class DatabaseModule extends AbstractModule {

    @Provides @Singleton
    public SQLClient getClient(FileConfiguration configuration) {
        ConfigurationSection section =
                configuration.getConfigurationSection("database");

        if (section == null) {
            throw new IllegalStateException("No database section found in config");
        }

        return new SQLClient.Builder("mariadb")
                .setDatabase(section.getString("database"))
                .setHost(section.getString("host"))
                .setPort(section.getInt("port"))
                .setUsername(section.getString("username"))
                .setPassword(section.getString("password"))
                .setMaximumPoolSize(section.getInt("maximum-pool-size"))
                .setDriverClassName("org.mariadb.jdbc.Driver")
                .build();
    }

}
