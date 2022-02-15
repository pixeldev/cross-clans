package net.cosmogrp.crclans.clan.recruitment;

import me.yushust.message.MessageHandler;
import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.channel.ChannelListener;

import javax.inject.Inject;

public class ClanRecruitmentChannel
        implements ChannelListener<RecruitmentMessage> {

    @Inject private MessageHandler messageHandler;

    @Override
    public void listen(
            Channel<RecruitmentMessage> channel,
            String server,
            RecruitmentMessage object
    ) {

    }
}
