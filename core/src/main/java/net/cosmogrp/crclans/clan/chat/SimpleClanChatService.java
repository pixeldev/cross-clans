package net.cosmogrp.crclans.clan.chat;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanChatService implements ClanChatService {

    @Inject private MessageHandler messageHandler;

    @Override
    public void setClanChat(Player player, User user) {

    }

    @Override
    public void setAlliesChat(Player player, User user) {

    }
}
