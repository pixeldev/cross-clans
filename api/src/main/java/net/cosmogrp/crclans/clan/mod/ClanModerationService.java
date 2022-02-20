package net.cosmogrp.crclans.clan.mod;

import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ClanModerationService {

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

}
