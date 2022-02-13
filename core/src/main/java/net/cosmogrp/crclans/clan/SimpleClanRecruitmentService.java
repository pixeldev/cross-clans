package net.cosmogrp.crclans.clan;

import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.user.ClusteredUser;
import org.bukkit.entity.Player;

public class SimpleClanRecruitmentService
        implements ClanRecruitmentService {

    @Override
    public void sendRecruitment(
            Clan clan, Player sender,
            ClusteredUser target
    ) {

    }
}
