package net.cosmogrp.crclans.clan.chat;

import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

public interface ClanChatService {

    void setClanChat(Player player, User user);

    void setAlliesChat(Player player, User user);

}
