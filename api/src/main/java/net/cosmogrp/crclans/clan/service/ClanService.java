package net.cosmogrp.crclans.clan.service;

import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ClanService<T extends Model & DocumentCodec> {

    @Nullable T getData(Player player, String tag);

    @Nullable T getData(String tag);

    void deleteSync(String tag);

    void createSync(Player creator, String tag);

    void save(Player player, T data);

    void save(T data);

    void saveSync(T data);

}
