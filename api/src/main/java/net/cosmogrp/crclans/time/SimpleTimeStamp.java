package net.cosmogrp.crclans.time;

import net.cosmogrp.storage.mongo.DocumentBuilder;
import org.bson.Document;

public class SimpleTimeStamp implements TimeStamp {

    protected final long timestamp;

    public SimpleTimeStamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getExpiration() {
        return timestamp;
    }

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() > timestamp;
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("timestamp", timestamp)
                .build();
    }
}
