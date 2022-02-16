package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class ClanMember implements DocumentCodec {

    private final UUID playerId;
    private final String playerName;
    private boolean moderator;
    private boolean online;

    private ClanMember(
            UUID playerId,
            String playerName,
            boolean moderator,
            boolean online
    ) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.moderator = moderator;
        this.online = online;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "ClanMember{" +
                "playerId=" + playerId +
                ", moderator=" + moderator +
                ", online=" + online +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanMember that = (ClanMember) o;
        return moderator == that.moderator &&
                online == that.online &&
                Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("playerId", playerId)
                .write("playerName", playerName)
                .write("moderator", moderator)
                .build();
    }

    public static ClanMember fromDocument(DocumentReader reader) {
        return new ClanMember(
                reader.readUuid("playerId"),
                reader.readString("playerName"),
                reader.readBoolean("moderator"),
                false
        );
    }

    public static ClanMember fromPlayer(Player player) {
        return new ClanMember(
                player.getUniqueId(),
                player.getName(),
                false,
                player.isOnline()
        );
    }
}
