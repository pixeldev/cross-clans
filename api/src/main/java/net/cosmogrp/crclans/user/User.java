package net.cosmogrp.crclans.user;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class User extends AbstractModel
        implements DocumentCodec {

    private final UUID playerId;

    private String clanTag;
    private String channelId;

    private User(UUID playerId, String clanTag) {
        super(playerId.toString());
        this.playerId = playerId;
        this.clanTag = clanTag;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setClan(@Nullable String tag) {
        this.clanTag = tag;
    }

    public @Nullable String getClanTag() {
        return clanTag;
    }

    public boolean hasClan() {
        return clanTag != null;
    }

    public @Nullable String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public static User create(UUID playerId) {
        return new User(playerId, null);
    }

    public static User fromDocument(DocumentReader reader) {
        return new User(
                reader.readUuid("_id"),
                reader.readString("clanTag")
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("clanTag", clanTag)
                .build();
    }
}
