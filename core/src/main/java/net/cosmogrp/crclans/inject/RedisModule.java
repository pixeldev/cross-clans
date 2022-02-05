package net.cosmogrp.crclans.inject;

import com.google.gson.Gson;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.redis.GsonRedis;
import net.cosmogrp.crclans.redis.JedisBuilder;
import net.cosmogrp.crclans.redis.JedisInstance;
import net.cosmogrp.crclans.redis.Redis;
import net.cosmogrp.crclans.redis.RedisCache;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import javax.inject.Singleton;

public class RedisModule extends AbstractModule {

    @Provides @Singleton
    public Redis getJedis(Plugin plugin, Gson gson) {
        FileConfiguration configuration = plugin.getConfig();
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
            jedisInstance = JedisBuilder.fromConfig(redisSection)
                    .build();
        }

        return GsonRedis.builder(plugin)
                .setParentChannel(configuration.getString("server-group") + "-crclans")
                .setGson(gson)
                .setJedis(jedisInstance)
                .build();
    }

    @Provides @Singleton
    public RedisCache getRedisCache(Redis redis) {
        return new RedisCache("crclans", redis.getRawConnection());
    }

}
