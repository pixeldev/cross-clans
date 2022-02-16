package net.cosmogrp.crclans.user.clan;

import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.channel.ChannelListener;

import javax.inject.Inject;

public class ClanKickChannelListener
        implements ChannelListener<ClanKickMessage> {

    @Inject private ClanUserService clanUserService;

    @Override
    public void listen(
            Channel<ClanKickMessage> channel,
            String server,
            ClanKickMessage object
    ) {
        clanUserService.notifyKick(
                object.getTarget(),
                object.getClanId()
        );
    }
}
