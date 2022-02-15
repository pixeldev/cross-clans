package net.cosmogrp.crclans.clan.recruitment;

import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.entity.Player;

public interface ClanRecruitmentService {

    void sendRecruitment(
            Player sender, User user,
            ClusteredUser target
    );

}
