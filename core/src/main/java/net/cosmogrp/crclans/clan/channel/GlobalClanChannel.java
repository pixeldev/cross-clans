package net.cosmogrp.crclans.clan.channel;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class GlobalClanChannel extends AbstractClanChannel {
    @Override
    public String getId() {
        return "global";
    }

    @Override
    public boolean sendMessage(String clanTag, Player sender, String message) {
        // do nothing, it's supposed to be minecraft's default chat
        return true;
    }

    @Override
    protected @Nullable Set<UUID> makeTargets(String clanTag) {
        return null;
    }
}
