package net.cosmogrp.crclans.clan.chat;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.channel.ClanChannel;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanChatService implements ClanChatService {

    @Inject private MessageHandler messageHandler;

    @Override
    public void setChannel(
            Player player, User user,
            ClanChannel clanChannel
    ) {
        String channelId = clanChannel.getId();

        if (channelId.equals("global")) {
            user.setChannelId(null);
            messageHandler.send(player, "user.exit-channel");
            return;
        }

        String currentChannel = user.getChannelId();

        if (currentChannel != null &&
                currentChannel.equals(channelId)) {
            messageHandler.send(player, "user.already-in-channel");
            return;
        }

        user.setChannelId(clanChannel.getId());
        messageHandler.sendReplacing(
                player, "user.change-channel",
                "%channel%",
                messageHandler.get(
                        player,
                        "channels." + channelId
                )
        );
    }

}
