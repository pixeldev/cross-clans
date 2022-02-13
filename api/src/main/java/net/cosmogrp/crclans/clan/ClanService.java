package net.cosmogrp.crclans.clan;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanService {

    void createClan(User user, Player owner, String tag);

    void deleteClan(User user, Player owner);

}
