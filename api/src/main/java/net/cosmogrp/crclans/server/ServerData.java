package net.cosmogrp.crclans.server;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public final class ServerData {

    private final String redisServer;
    private String bungeeIdentifier;

    public ServerData(FileConfiguration configuration) {
        this.redisServer = UUID.randomUUID().toString();
        this.bungeeIdentifier = configuration.getString("bungee-server");
    }

    public String getRedisServer() {
        return redisServer;
    }

    public void setBungeeIdentifier(String bungeeIdentifier) {
        this.bungeeIdentifier = bungeeIdentifier;
    }

    public String getBungeeIdentifier() {
        return bungeeIdentifier;
    }
}
