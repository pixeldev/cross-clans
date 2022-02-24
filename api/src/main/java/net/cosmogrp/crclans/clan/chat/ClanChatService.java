package net.cosmogrp.crclans.clan.chat;

import net.cosmogrp.crclans.clan.channel.ClanChannel;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanChatService {

    void setChannel(
            Player player, User user,
            ClanChannel clanChannel
    );

}
