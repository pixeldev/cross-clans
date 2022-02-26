package net.cosmogrp.crclans.clan.enemy;

import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.service.DomainClanService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanEnemyService extends DomainClanService<ClanEnemyData> {

    String KEY = "clan-enemy";

    void addEnemy(
            Player sender, User user,
            ClanMemberData targetMemberData
    );

    void removeEnemy(
            Player sender, User user,
            ClanMemberData targetMemberData
    );

}
