package net.cosmogrp.crclans.user.cluster;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ClusteredUserRegistry {

    Collection<String> getClusteredUsers();

    @Nullable ClusteredUser find(String name);

    default @Nullable ClusteredUser find(Player player) {
        return find(player.getName());
    }

    void create(Player player);

    void delete(Player player);

}
