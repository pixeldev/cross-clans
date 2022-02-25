package net.cosmogrp.crclans.time;

import java.util.Map;

public class TimeStamped<T> {

    private final long time;
    protected final Map<T, Long> timestamps;

    protected TimeStamped(long time, Map<T, Long> timestamps) {
        this.time = time;
        this.timestamps = timestamps;
    }

    public long getTimestamp(T key) {
        Long timestamp = timestamps.get(key);

        if (timestamp == null) {
            return -1;
        }

        return timestamp;
    }

    public void bind(T key) {
        timestamps.put(key, System.currentTimeMillis() + time);
    }

    public void unbind(T key) {
        timestamps.remove(key);
    }

    public boolean hasExpired(T key) {
        long timestamp = getTimestamp(key);

        if (timestamp <= 0) {
            return true;
        }

        return System.currentTimeMillis() > timestamp;
    }
}
