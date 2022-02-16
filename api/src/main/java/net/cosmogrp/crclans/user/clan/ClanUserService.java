package net.cosmogrp.crclans.user.clan;

import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ClanUserService {

    void connect(Player player, User user);

    void disconnect(User user);

    @Nullable Clan getClan(Player player, User user);

    void disbandClan(Player player, User user);

    void leaveClan(Player player, User user);

    void executeAsOwner(
            Player player, User user,
            Consumer<Clan> consumer
    );

    void computeAsOwner(
            Player player, User user,
            Consumer<Clan> consumer
    );

}
