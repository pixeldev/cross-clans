package net.cosmogrp.crclans.clan.home;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanHomeService {

    void setHome(Player player, User user);

    void delHome(Player player, User user);

    void teleportToHome(Player player, User user);

}