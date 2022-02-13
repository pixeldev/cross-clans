package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanMember implements DocumentCodec {

    private final UUID playerId;
    private boolean moderator;
    private boolean online;

    private ClanMember(
            UUID playerId,
            boolean moderator,
            boolean online
    ) {
        this.playerId = playerId;
        this.moderator = moderator;
        this.online = online;
    }

    public UUID getPlayerId() {
        return playerId;
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
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("playerId", playerId)
                .write("moderator", moderator)
                .build();
    }

    public static ClanMember fromDocument(DocumentReader reader) {
        return new ClanMember(
                reader.readUuid("playerId"),
                reader.readBoolean("moderator"),
                false
        );
    }

    public static ClanMember fromPlayer(Player player) {
        return new ClanMember(
                player.getUniqueId(),
                false,
                player.isOnline()
        );
    }
}
