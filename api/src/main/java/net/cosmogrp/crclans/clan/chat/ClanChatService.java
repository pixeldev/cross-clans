package net.cosmogrp.crclans.clan.chat;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanChatService {

    void setChannel(
            Player player, User user,
            String channelId
    );

    void toggleChannel(
            Player player, User user,
            String channelId
    );

}
