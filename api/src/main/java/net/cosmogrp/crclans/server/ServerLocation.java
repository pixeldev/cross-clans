package net.cosmogrp.crclans.server;

import net.cosmogrp.crclans.location.PlayerAxis;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ServerLocation implements DocumentCodec {

    private final String serverName;
    private final PlayerAxis playerAxis;

    private ServerLocation(
            String serverName,
            PlayerAxis playerAxis
    ) {
        this.serverName = serverName;
        this.playerAxis = playerAxis;
    }

    public String getServerName() {
        return serverName;
    }

    public PlayerAxis getPlayerAxis() {
        return playerAxis;
    }

    public static ServerLocation centered(
            ServerData serverData,
            Player player
    ) {
        return new ServerLocation(
                serverData.getBungeeIdentifier(),
                PlayerAxis.centered(player)
        );
    }

    public static ServerLocation exactly(
            ServerData serverData,
            Player player
    ) {
        return new ServerLocation(
                serverData.getBungeeIdentifier(),
                PlayerAxis.exactly(player)
        );
    }

    public static ServerLocation fromDocument(DocumentReader reader) {
        return new ServerLocation(
                reader.readString("serverName"),
                reader.readChild("playerAxis", PlayerAxis::fromDocument)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerLocation that = (ServerLocation) o;
        return Objects.equals(serverName, that.serverName) &&
                Objects.equals(playerAxis, that.playerAxis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverName, playerAxis);
    }

    @Override
    public String toString() {
        return "ServerLocation{" +
                "serverName='" + serverName + '\'' +
                ", playerAxis=" + playerAxis +
                '}';
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("serverName", serverName)
                .write("playerAxis", playerAxis)
                .build();
    }
}
