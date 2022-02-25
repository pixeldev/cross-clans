package net.cosmogrp.crclans.time;

import net.cosmogrp.storage.mongo.DocumentCodec;
import org.jetbrains.annotations.Nullable;

public interface TimeStamped extends DocumentCodec {

    @Nullable TimeStamp getTimestamp(String key);

    void bind(String key, long duration);

    void unbind(String key);

    boolean hasExpired(String key);

}
