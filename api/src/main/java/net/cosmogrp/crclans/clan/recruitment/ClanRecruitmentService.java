package net.cosmogrp.crclans.clan.recruitment;

import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.user.ClusteredUser;
import org.bukkit.entity.Player;

public interface ClanRecruitmentService {

    void sendRecruitment(
            Clan clan, Player sender,
            ClusteredUser target
    );

}
