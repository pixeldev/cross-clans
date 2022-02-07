package net.cosmogrp.crclans.messenger;

import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface Notifier {

    void notify(@Nullable Set<UUID> targets, String path, Object... parameters);

    default void notify(String path, Object... parameters) {
        notify(null, path, parameters);
    }

}
