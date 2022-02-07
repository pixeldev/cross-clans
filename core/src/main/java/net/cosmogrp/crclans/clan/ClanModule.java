package net.cosmogrp.crclans.clan;

import com.google.gson.Gson;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.inject.scope.Scopes;
import net.cosmogrp.storage.AsyncModelService;
import net.cosmogrp.storage.model.meta.ModelMeta;
import net.cosmogrp.storage.redis.RedisModelService;
import net.cosmogrp.storage.redis.connection.RedisCache;
import net.cosmogrp.storage.sql.SQLModelService;
import net.cosmogrp.storage.sql.connection.SQLClient;
import net.cosmogrp.storage.sql.identity.DataType;
import net.cosmogrp.storage.sql.identity.SQLConstraint;
import net.cosmogrp.storage.sql.identity.SQLMapSerializer;
import net.cosmogrp.storage.sql.identity.Table;
import net.cosmogrp.storage.sql.mysql.MySQLElement;
import net.cosmogrp.storage.sql.mysql.MySQLTable;
import org.bukkit.configuration.file.FileConfiguration;
import org.jdbi.v3.core.mapper.RowMapper;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

public class ClanModule extends AbstractModule {

    @Override
    public void configure() {
        bind(ClanService.class).to(SimpleClanService.class).in(Scopes.SINGLETON);
    }

    @Provides @Singleton
    public AsyncModelService<Clan> createService(
            Executor executor,
            FileConfiguration configuration,
            SQLClient client,
            Gson gson,
            RedisCache redisCache
    ) {
        ModelMeta<Clan> modelMeta = new ModelMeta<>(Clan.class);
        Table table = new MySQLTable(
                configuration.getString("server-group") + "clans",
                Arrays.asList(
                        new MySQLElement("id", DataType.STRING, SQLConstraint.PRIMARY, SQLConstraint.NOT_NULL),
                        new MySQLElement("description", DataType.STRING),
                        new MySQLElement("ownerId", DataType.STRING)
                ));

        modelMeta.addProperty("redis-table", "clans");
        modelMeta.addProperty("sql-table", table);

        RowMapper<Clan> mapper = (rs, ctx) -> new Clan(
                rs.getString("id"),
                UUID.fromString(rs.getString("ownerId")),
                rs.getString("description"),
                new HashSet<>()
        );

        SQLMapSerializer<Clan> mapSerializer = clan -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", clan.getId());
            map.put("ownerId", clan.getOwnerId().toString());
            map.put("description", clan.getDescription());
            return map;
        };

        return new SQLModelService<>(
                executor, modelMeta,
                new RedisModelService<>(executor, modelMeta, gson, redisCache),
                client,
                mapper, mapSerializer
        );
    }

}
