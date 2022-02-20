package net.cosmogrp.crclans.clan.mod;

import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.channel.ChannelListener;

import javax.inject.Inject;

public class ClanKickChannelListener
        implements ChannelListener<ClanKickMessage> {

    @Inject private ClanModerationService clanModerationService;

    @Override
    public void listen(
            Channel<ClanKickMessage> channel,
            String server,
            ClanKickMessage object
    ) {
        clanModerationService.notifyKick(
                object.getTarget(),
                object.getClanId()
        );
    }
}
