package net.cosmogrp.crclans.time;

import net.cosmogrp.storage.mongo.DocumentCodec;

public interface TimeStamp extends DocumentCodec {

    long getExpiration();

    boolean hasExpired();

}
