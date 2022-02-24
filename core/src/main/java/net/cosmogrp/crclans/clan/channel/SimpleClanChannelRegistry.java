package net.cosmogrp.crclans.clan.channel;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleClanChannelRegistry implements ClanChannelRegistry {

    private final Map<String, ClanChannel> channels;

    public SimpleClanChannelRegistry() {
        this.channels = new HashMap<>();
    }

    @Override
    public Collection<String> getChannels() {
        return channels.keySet();
    }

    @Override
    public @Nullable ClanChannel getChannel(String id) {
        return channels.get(id);
    }

    @Override
    public void registerChannel(ClanChannel channel) {
        channels.put(channel.getId(), channel);
    }
}
