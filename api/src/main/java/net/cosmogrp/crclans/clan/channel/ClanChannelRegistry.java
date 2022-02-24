package net.cosmogrp.crclans.clan.channel;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ClanChannelRegistry {

    Collection<String> getChannels();

    @Nullable ClanChannel getChannel(String id);

    void registerChannel(ClanChannel channel);

}
