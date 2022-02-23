package net.cosmogrp.crclans.clan.member;

import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.channel.ChannelListener;

import javax.inject.Inject;

public class ClanKickChannelListener
        implements ChannelListener<ClanKickMessage> {

    @Inject private ClanMemberService memberService;

    @Override
    public void listen(
            Channel<ClanKickMessage> channel,
            String server,
            ClanKickMessage object
    ) {
        memberService.notifyKick(
                object.getTarget(),
                object.getClanId()
        );
    }
}
