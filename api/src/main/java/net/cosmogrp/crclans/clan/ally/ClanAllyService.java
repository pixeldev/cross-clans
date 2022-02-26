package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanAllyService extends ClanService<ClanAllyData> {

    String KEY = "clan-ally";

    void sendAllies(Player sender, User user);

    void removeAlly(
            Player sender, User user,
            ClanAllyData allyData
    );

}
