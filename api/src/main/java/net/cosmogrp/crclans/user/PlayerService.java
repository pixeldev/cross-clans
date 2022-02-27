package net.cosmogrp.crclans.user;

import org.bukkit.entity.Player;

public interface PlayerService {

    void load(Player player);

    void save(Player player);

    void saveAll();

}
