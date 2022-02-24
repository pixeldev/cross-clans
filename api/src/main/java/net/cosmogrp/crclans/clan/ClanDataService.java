package net.cosmogrp.crclans.clan;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanDataService
        extends ClanService<ClanData> {

    String KEY = "clan-data";

    void createClan(User user, Player owner, String tag);

}
