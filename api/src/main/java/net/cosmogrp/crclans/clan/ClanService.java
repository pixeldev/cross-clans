package net.cosmogrp.crclans.clan;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ClanService {

    @Nullable Clan getClan(String tag);

    void createClan(User user, Player owner, String tag);

    void deleteClan(User user, Player owner);

}
