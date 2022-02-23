package net.cosmogrp.crclans.clan.member;

import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public interface ClanMemberService
        extends ClanService<ClanMemberData> {

    String KEY = "clan-member";

    void kick(
            Player player, User user,
            ClanMemberData data, ClanMember target
    );

    void promote(
            Player player, User user,
            ClanMemberData data, ClanMember target
    );

    void demote(
            Player player, User user,
            ClanMemberData data, ClanMember target
    );

    boolean notifyKick(UUID targetId, String clanId);

    void computeAsOwner(
            Player player, User user,
            Consumer<ClanMemberData> action
    );

}
