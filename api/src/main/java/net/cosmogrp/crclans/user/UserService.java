package net.cosmogrp.crclans.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface UserService {

    @Nullable User getUser(Player player);

    void loadOrCreate(Player player);

}
