package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanAllyRequestService
        extends ClanService<ClanAllyRequestData> {

    String KEY = "clan-ally-request";

    void sendAllyRequest(
            Player player, User user,
            String target
    );

    void acceptAlly(
            Player player, User user,
            ClanAllyRequestData data
    );

    void denyAlly(
            Player player, User user,
            ClanAllyRequestData data
    );

}
