package net.cosmogrp.crclans.clan;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface ClanDataService
        extends ClanService<ClanData> {

    void createClan(User user, Player owner, String tag);

    void computeAsOwner(
            Player player, User user,
            Consumer<ClanData> action
    );

}
