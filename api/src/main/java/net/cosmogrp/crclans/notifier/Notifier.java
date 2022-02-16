package net.cosmogrp.crclans.notifier;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface Notifier {

    void notifyIn(
            @Nullable Set<UUID> targets,
            @Nullable String mode,
            String path, Object... parameters
    );

    default void notify(
            @Nullable Set<UUID> targets,
            String path, Object... parameters) {
        notifyIn(targets, null, path, parameters);
    }

    default void singleNotify(UUID target, String path, Object... parameters) {
        notifyIn(Sets.newHashSet(target), null, path, parameters);
    }

    default void singleNotifyIn(
            UUID target, String mode,
            String path, Object... parameters
    ) {
        notifyIn(Sets.newHashSet(target), mode, path, parameters);
    }

    default void notify(String path, Object... parameters) {
        notifyIn(null, null, path, parameters);
    }

    default void notifyIn(String mode, String path, Object... parameters) {
        notifyIn(null, mode, path, parameters);
    }

}
