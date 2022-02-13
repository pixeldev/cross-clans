package net.cosmogrp.crclans.inject;

import com.google.gson.Gson;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.server.ServerData;
import net.cosmogrp.storage.redis.connection.GsonRedis;
import net.cosmogrp.storage.redis.connection.JedisBuilder;
import net.cosmogrp.storage.redis.connection.JedisInstance;
import net.cosmogrp.storage.redis.connection.Redis;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class RedisModule extends AbstractModule {

    @Provides @Singleton
    public Redis getJedis(
            FileConfiguration configuration,
            ServerData serverData,
            Executor executor,
            Gson gson
    ) {
        ConfigurationSection redisSection =
                configuration.getConfigurationSection("redis");

        JedisInstance jedisInstance;

        if (redisSection == null) {
            jedisInstance = JedisBuilder.builder()
                    .setTimeout(2000)
                    .setHost("localhost")
                    .setPort(6379)
                    .setPassword("")
                    .build();
        } else {
            jedisInstance = JedisBuilder.builder()
                    .setTimeout(redisSection.getInt("timeout"))
                    .setHost(redisSection.getString("host"))
                    .setPort(redisSection.getInt("port"))
                    .setPassword(redisSection.getString("password"))
                    .build();
        }

        return GsonRedis.builder(executor)
                .setParentChannel(configuration.getString("server-group") + "-crclans")
                .setGson(gson)
                .setServerId(serverData.getRedisServer())
                .setJedis(jedisInstance)
                .build();
    }

    @Provides @Singleton
    public RedisCache getRedisCache(
            FileConfiguration configuration,
            Redis redis
    ) {
        return new RedisCache(
                configuration.getString("server-group") + "-crclans",
                redis.getRawConnection()
        );
    }

}
