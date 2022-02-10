package net.cosmogrp.crclans.user;

import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class User extends AbstractModel
        implements DocumentCodec {

    private final UUID playerId;

    private String clanTag;

    private User(UUID playerId, String clanTag) {
        super(playerId.toString());
        this.playerId = playerId;
        this.clanTag = clanTag;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setClan(@Nullable Clan clan) {
        if (clan == null) {
            this.clanTag = null;
        } else {
            this.clanTag = clan.getId();
        }
    }

    public @Nullable String getClanTag() {
        return clanTag;
    }

    public boolean hasClan() {
        return clanTag != null;
    }

    public static User create(UUID playerId) {
        return new User(playerId, null);
    }

    public static User fromDocument(Document document) {
        return new User(
                UUID.fromString(document.getString("_id")),
                document.getString("clanTag")
        );
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        document.put("_id", playerId.toString());
        document.put("clanTag", clanTag);
        return document;
    }
}
