package net.cosmogrp.crclans.time;

import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SimpleTimeStamped implements TimeStamped {

    protected final Map<String, RelationalTimeStamp> timestamps;

    private SimpleTimeStamped(Map<String, RelationalTimeStamp> timestamps) {
        this.timestamps = timestamps;
    }

    @Override
    public @Nullable TimeStamp getTimestamp(String key) {
        return timestamps.get(key);
    }

    @Override
    public void bind(String key, long duration) {
        timestamps.put(key, RelationalTimeStamp.now(key, duration));
    }

    @Override
    public void unbind(String key) {
        timestamps.remove(key);
    }

    @Override
    public boolean hasExpired(String key) {
        TimeStamp timestamp = getTimestamp(key);

        if (timestamp == null) {
            return true;
        }

        return timestamp.hasExpired();
    }

    public static SimpleTimeStamped create() {
        return new SimpleTimeStamped(new HashMap<>());
    }

    public static SimpleTimeStamped fromDocument(DocumentReader reader) {
        return new SimpleTimeStamped(
                reader.readMap(
                        "timestamps",
                        RelationalTimeStamp::getKey,
                        RelationalTimeStamp::fromDocument
                )
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("timestamps", timestamps)
                .build();
    }
}
