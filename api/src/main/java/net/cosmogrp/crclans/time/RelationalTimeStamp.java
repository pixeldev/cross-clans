package net.cosmogrp.crclans.time;

import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;

public class RelationalTimeStamp extends SimpleTimeStamp {

    private final String key;

    private RelationalTimeStamp(String key, long timestamp) {
        super(timestamp);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static RelationalTimeStamp now(String key, long duration) {
        return create(
                key,
                System.currentTimeMillis() +
                        (duration * 1000));
    }

    public static RelationalTimeStamp create(String key, long timestamp) {
        return new RelationalTimeStamp(key, timestamp);
    }

    public static RelationalTimeStamp fromDocument(DocumentReader reader) {
        return new RelationalTimeStamp(
                reader.readString("key"),
                reader.readLong("timestamp")
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("key", key)
                .write("timestamp", timestamp)
                .build();
    }
}
