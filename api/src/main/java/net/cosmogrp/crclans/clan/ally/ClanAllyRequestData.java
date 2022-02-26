package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.time.SimpleTimeStamped;
import net.cosmogrp.crclans.time.TimeStamp;
import net.cosmogrp.crclans.time.TimeStamped;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

public class ClanAllyRequestData extends AbstractModel
        implements DocumentCodec, TimeStamped {

    private final TimeStamped timeStamped;

    public ClanAllyRequestData(String id, TimeStamped timeStamped) {
        super(id);
        this.timeStamped = timeStamped;
    }

    @Override
    public @Nullable TimeStamp getTimestamp(String key) {
        return timeStamped.getTimestamp(key);
    }

    @Override
    public void bind(String key, long duration) {
        timeStamped.bind(key, duration);
    }

    @Override
    public void unbind(String key) {
        timeStamped.unbind(key);
    }

    @Override
    public boolean hasExpired(String key) {
        return timeStamped.hasExpired(key);
    }

    public static ClanAllyRequestData create(String tag) {
        return new ClanAllyRequestData(tag, SimpleTimeStamped.create());
    }

    public static ClanAllyRequestData fromDocument(DocumentReader reader) {
        return new ClanAllyRequestData(
                reader.readString("_id"),
                reader.readChild("data", SimpleTimeStamped::fromDocument)
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("data", timeStamped)
                .build();
    }
}
