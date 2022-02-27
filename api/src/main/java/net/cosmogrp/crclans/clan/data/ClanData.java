package net.cosmogrp.crclans.clan.data;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class ClanData extends AbstractModel
        implements DocumentCodec {

    private final Date creation;
    private String description;
    private boolean friendlyFire;

    private ClanData(
            String id, Date creation,
            String description, boolean friendlyFire
    ) {
        super(id);
        this.creation = creation;
        this.description = description;
        this.friendlyFire = friendlyFire;
    }

    public Date getCreation() {
        return creation;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public static ClanData create(String tag) {
        return new ClanData(
                tag, new Date(),
                null,
                false
        );
    }

    public static ClanData fromDocument(DocumentReader reader) {
        return new ClanData(
                reader.readString("_id"),
                reader.readDate("creation"),
                reader.readString("description"),
                reader.readBoolean("friendlyFire")
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("creation", creation)
                .write("description", description)
                .write("friendlyFire", friendlyFire)
                .build();
    }
}
