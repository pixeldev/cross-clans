package net.cosmogrp.crclans.clan.channel;

import me.yushust.inject.Injector;
import net.cosmogrp.crclans.loader.Loader;

import javax.inject.Inject;

public class DefaultChannelLoader implements Loader {

    @Inject private Injector injector;
    @Inject private ClanChannelRegistry channelRegistry;

    @Override
    public void load() {
        channelRegistry.registerChannel(injector.getInstance(MemberClanChannel.class));
        channelRegistry.registerChannel(injector.getInstance(GlobalClanChannel.class));
    }
}
