package net.cosmogrp.crclans.clan.recruitment;

import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.entity.Player;

public interface ClanRecruitmentService
        extends ClanService<ClanRecruitmentData> {

    String KEY = "clan-recruitment";

    void sendRecruitment(
            Player sender, User user,
            ClusteredUser target
    );

    void acceptRecruitment(
            Player sender, User user,
            ClanRecruitmentData data
    );

    void denyRecruitment(
            Player sender,
            ClanRecruitmentData clan
    );

}
