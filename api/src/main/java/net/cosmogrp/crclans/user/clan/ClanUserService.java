package net.cosmogrp.crclans.user.clan;

import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClanUserService {

    void connect(Player player, User user);

    void disconnect(User user);

    @Nullable Clan getClan(Player player, User user);

    void setHome(Player player, User user);

    void delHome(Player player, User user);

    void teleportToHome(Player player, User user);

    void kickMember(
            Player player, User user,
            Clan clan, ClanMember target
    );

    void promoteMember(
            Player player, User user,
            Clan clan, ClanMember target
    );

    void demoteMember(
            Player player, User user,
            Clan clan, ClanMember target
    );

    boolean notifyKick(UUID targetId, String clanId);

    void disbandClan(Player player, User user);

    boolean notifyDisband(Set<UUID> onlineMembers);

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
