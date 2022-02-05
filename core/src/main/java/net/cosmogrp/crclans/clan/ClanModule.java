package net.cosmogrp.crclans.clan;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.storage.dist.RemoteModelService;
import net.cosmogrp.storage.model.exception.NoSuchModelDataException;
import net.cosmogrp.storage.sql.SQLModelService;
import net.cosmogrp.storage.sql.connection.SQLClient;
import net.cosmogrp.storage.sql.identity.SQLMapSerializer;
import net.cosmogrp.storage.sql.meta.SQLModelMeta;
import org.jdbi.v3.core.mapper.RowMapper;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

public class ClanModule extends AbstractModule {

    @Provides @Singleton
    public RemoteModelService<Clan> createService(
            Executor executor,
            SQLClient client
    ) throws NoSuchModelDataException {
        SQLModelMeta<Clan> meta = new SQLModelMeta<>(Clan.class);
        RowMapper<Clan> mapper = (rs, ctx) -> new Clan(
                rs.getString("id"),
                UUID.fromString(rs.getString("ownerId")),
                rs.getString("name"),
                new HashSet<>()
        );

        SQLMapSerializer<Clan> mapSerializer = clan -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", clan.getId());
            map.put("ownerId", clan.getOwnerId().toString());
            map.put("name", clan.getName());
            return map;
        };

        return new SQLModelService<>(
                executor, meta, client,
                mapper, mapSerializer
        );
    }

}
