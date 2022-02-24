package net.cosmogrp.crclans.listener;

import net.cosmogrp.crclans.clan.channel.ClanChannel;
import net.cosmogrp.crclans.clan.channel.ClanChannelRegistry;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.inject.Inject;

public class PlayerChatListener implements Listener {

    @Inject private UserService userService;
    @Inject private ClanChannelRegistry channelRegistry;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = userService.getUser(player);

        if (user == null) {
            return;
        }

        String clanTag = user.getClanTag();

        if (clanTag == null) {
            return;
        }

        String channelId = user.getChannelId();

        if (channelId == null) {
            return;
        }

        ClanChannel clanChannel = channelRegistry.getChannel(channelId);

        if (clanChannel != null) {
            if (clanChannel.sendMessage(
                    clanTag, player,
                    event.getMessage()
            )) {
                event.setCancelled(true);
            }
        }
    }

}
