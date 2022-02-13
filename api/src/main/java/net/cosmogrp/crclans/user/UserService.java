package net.cosmogrp.crclans.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface UserService {

    @Nullable User getUser(Player player);

    @Nullable User getUser(UUID playerId);

    /**
     * Tries to get the user from redis
     * if it's cached or from the database
     * if it's not cached, if it's not in the database
     * it will be created and local cached.
     *
     * NOTE: This method must be called from an async thread
     * preferably from {@link org.bukkit.event.player.AsyncPlayerPreLoginEvent}
     * @param playerId the player's UUID
     * @return a nullable kick reason if the user is not found
     */
    @Nullable String loadOrCreate(UUID playerId);

    void saveUser(Player player);

}
