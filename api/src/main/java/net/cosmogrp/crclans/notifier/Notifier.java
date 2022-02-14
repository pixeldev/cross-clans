package net.cosmogrp.crclans.notifier;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface Notifier {

    void notify(@Nullable Set<UUID> targets, String path, Object... parameters);

    default void singleNotify(UUID target, String path, Object... parameters) {
        notify(Sets.newHashSet(target), path, parameters);
    }

    default void notify(String path, Object... parameters) {
        notify(null, path, parameters);
    }

}
