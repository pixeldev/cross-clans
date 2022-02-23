package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.Model;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ClanService<T extends Model> {

    @Nullable T getData(Player player, String tag);

    @Nullable T getData(String tag);

    void save(Player player, T data);

    void save(T data);

}
