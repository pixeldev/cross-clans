package net.cosmogrp.crclans.clan;

import org.bukkit.entity.Player;

public interface ClanService {

    void createClan(Player owner, String tag);

    void deleteClan(Player owner);

}
