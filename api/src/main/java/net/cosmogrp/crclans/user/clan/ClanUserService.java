package net.cosmogrp.crclans.user.clan;

import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ClanUserService {

    @Nullable Clan getClan(Player player, User user);

    void executeAsOwner(
            Player player, User user,
            Consumer<Clan> consumer
    );

}
