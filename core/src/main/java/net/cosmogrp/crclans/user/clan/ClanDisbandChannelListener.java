package net.cosmogrp.crclans.user.clan;

import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.channel.ChannelListener;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

public class ClanDisbandChannelListener
        implements ChannelListener<ClanDisbandMessage> {

    @Inject private ClanUserService clanUserService;

    @Override
    public void listen(
            Channel<ClanDisbandMessage> channel,
            String server,
            ClanDisbandMessage object
    ) {
        Set<UUID> onlineMembers = object.getOnlineMembers();
        clanUserService.notifyDisband(onlineMembers);
    }
}
