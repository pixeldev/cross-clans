package net.cosmogrp.crclans.user;

import net.cosmogrp.crclans.server.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ClusteredUser {

    private final UUID playerId;
    private final ServerData serverData;

    public ClusteredUser(
            UUID playerId,
            ServerData serverData
    ) {
        this.playerId = playerId;
        this.serverData = serverData;
    }

    public OfflinePlayer asPlayer() {
        return Bukkit.getOfflinePlayer(playerId);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public ServerData getServerData() {
        return serverData;
    }
}
