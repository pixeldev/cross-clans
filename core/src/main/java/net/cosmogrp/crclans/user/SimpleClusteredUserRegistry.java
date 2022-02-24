package net.cosmogrp.crclans.user;

import com.google.gson.Gson;
import net.cosmogrp.crclans.server.ServerData;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Collection;

public class SimpleClusteredUserRegistry
        implements ClusteredUserRegistry {

    @Inject private ServerData serverData;
    @Inject private RedisCache redisCache;
    @Inject private Gson gson;

    @Override
    public Collection<String> getClusteredUsers() {
        return redisCache.getAllKeys("players");
    }

    @Override
    public @Nullable ClusteredUser find(String name) {
        String json = redisCache.get("players", name);

        if (json == null) {
            return null;
        }

        ClusteredUser clusteredUser = gson.fromJson(json, ClusteredUser.class);
        OfflinePlayer offlinePlayer = clusteredUser.asPlayer();

        if (offlinePlayer == null
                || !offlinePlayer.hasPlayedBefore()) {
            return null;
        }

        return clusteredUser;
    }

    @Override
    public void create(Player player) {
        redisCache.set(
                "players", player.getName(),
                gson.toJson(new ClusteredUser(
                        player.getUniqueId(), serverData
                )));
    }

    @Override
    public void delete(Player player) {
        redisCache.del("players", player.getName());
    }
}
