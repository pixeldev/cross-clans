package net.cosmogrp.crclans.clan.channel;

import org.bukkit.entity.Player;

public interface ClanChannel {

    String getId();

    boolean sendMessage(
            String clanTag, Player sender,
            String message
    );

}
