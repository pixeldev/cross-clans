package net.cosmogrp.crclans.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    @Nullable User getUser(Player player);

    @Nullable User getUser(UUID playerId);

    CompletableFuture<User> loadOrCreate(Player player);

    User saveUser(Player player);

    User forcedSave(Player player);

}
