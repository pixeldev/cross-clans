package net.cosmogrp.crclans.clan.chat;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.channel.GlobalClanChannel;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanChatService implements ClanChatService {

    @Inject private MessageHandler messageHandler;

    @Override
    public void setChannel(
            Player player, User user,
            String channelId
    ) {
        String tag = user.getClanTag();

        if (tag == null) {
            messageHandler.send(player, "clan.not-in-clan");
            return;
        }

        if (channelId.equals(GlobalClanChannel.ID)) {
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

        user.setChannelId(channelId);
        messageHandler.sendReplacing(
                player, "user.change-channel",
                "%channel%",
                messageHandler.get(
                        player,
                        "channels." + channelId
                )
        );
    }

    @Override
    public void toggleChannel(
            Player player, User user,
            String channelId
    ) {
        String currentChannel = user.getChannelId();

        if (currentChannel != null &&
                currentChannel.equals(channelId)) {
            setChannel(player, user, GlobalClanChannel.ID);
            return;
        }

        setChannel(player, user, channelId);
    }

}
