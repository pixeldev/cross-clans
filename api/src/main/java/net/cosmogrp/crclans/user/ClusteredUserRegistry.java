package net.cosmogrp.crclans.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ClusteredUserRegistry {

    @Nullable ClusteredUser find(String name);

    default @Nullable ClusteredUser find(Player player) {
        return find(player.getName());
    }

    void create(Player player);

    void delete(Player player);

}
