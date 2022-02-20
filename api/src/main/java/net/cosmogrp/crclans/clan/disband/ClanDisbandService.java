package net.cosmogrp.crclans.clan.disband;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface ClanDisbandService {

    void disbandClan(Player player, User user);

    boolean notifyDisband(Set<UUID> onlineMembers);

    void leaveClan(Player player, User user);

}
