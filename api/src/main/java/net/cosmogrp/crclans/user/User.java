package net.cosmogrp.crclans.user;

import net.cosmogrp.crclans.clan.ClanData;
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
    private ChatType chatType;

    private User(UUID playerId, String clanTag) {
        super(playerId.toString());
        this.playerId = playerId;
        this.clanTag = clanTag;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public @Nullable ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public void setClan(@Nullable ClanData clan) {
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
