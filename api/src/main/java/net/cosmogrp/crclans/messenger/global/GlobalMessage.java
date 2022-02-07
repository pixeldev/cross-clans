package net.cosmogrp.crclans.messenger.global;

import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class GlobalMessage {

    private final String path;
    private final Object[] replacements;
    private final Set<UUID> targets;

    private GlobalMessage(
            String path,
            Object[] replacements,
            Set<UUID> targets
    ) {
        this.path = path;
        this.replacements = replacements;
        this.targets = targets;
    }

    public String getPath() {
        return path;
    }

    public Object[] getReplacements() {
        return replacements;
    }

    public @Nullable Set<UUID> getTargets() {
        return targets;
    }

    public static GlobalMessage everyone(String path, Object... replacements) {
        return new GlobalMessage(path, replacements, null);
    }

    public static GlobalMessage targets(
            Set<UUID> targets,
            String path,
            Object... replacements
    ) {
        return new GlobalMessage(path, replacements, targets);
    }
}
