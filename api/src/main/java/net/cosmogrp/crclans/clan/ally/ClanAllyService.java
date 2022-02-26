package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.service.DomainClanService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanAllyService extends DomainClanService<ClanAllyData> {

    String KEY = "clan-ally";

    void removeAlly(
            Player sender, User user,
            ClanAllyData allyData
    );

    void removeAlly(String source, String target);

}
