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

    private ClanData(
            String id, Date creation,
            String description
    ) {
        super(id);
        this.creation = creation;
        this.description = description;
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

    public static ClanData create(String tag) {
        return new ClanData(
                tag, new Date(),
                null
        );
    }

    public static ClanData fromDocument(DocumentReader reader) {
        return new ClanData(
                reader.readString("_id"),
                reader.readDate("creation"),
                reader.readString("description")
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("creation", creation)
                .write("description", description)
                .build();
    }
}
