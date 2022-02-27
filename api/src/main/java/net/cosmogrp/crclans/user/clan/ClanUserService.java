package net.cosmogrp.crclans.user.clan;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanUserService {

    void connect(Player player, User user);

    void disconnect(User user);

    void forceDisconnect(User user);

}
