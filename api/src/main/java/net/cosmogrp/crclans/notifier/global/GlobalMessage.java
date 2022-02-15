package net.cosmogrp.crclans.notifier.global;

import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class GlobalMessage {

    private final String path;
    private final String mode;
    private final Object[] replacements;
    private final Set<UUID> targets;

    private GlobalMessage(
            String path,
            String mode, Object[] replacements,
            Set<UUID> targets
    ) {
        this.path = path;
        this.mode = mode;
        this.replacements = replacements;
        this.targets = targets;
    }

    public String getPath() {
        return path;
    }

    public String getMode() {
        return mode;
    }

    public Object[] getReplacements() {
        return replacements;
    }

    public @Nullable Set<UUID> getTargets() {
        return targets;
    }

    public static GlobalMessage create(
            @Nullable Set<UUID> targets,
            @Nullable String mode,
            String path,
            Object... replacements
    ) {
        return new GlobalMessage(path, mode, replacements, targets);
    }
}
