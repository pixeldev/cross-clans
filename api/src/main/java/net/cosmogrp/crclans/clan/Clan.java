package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.AbstractModel;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Clan extends AbstractModel {

    private final UUID ownerId;
    private final String description;
    private final Set<UUID> members;

    public Clan(String tag, String description, Player owner) {
        super(tag);
        this.description = description;
        this.ownerId = owner.getUniqueId();
        this.members = new HashSet<>();
    }

    public Clan(
            String id, UUID ownerId,
            String description,
            Set<UUID> members
    ) {
        super(id);
        this.ownerId = ownerId;
        this.description = description;
        this.members = members;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Clan{" +
                "ownerId=" + ownerId +
                ", name='" + description + '\'' +
                ", members=" + members +
                '}';
    }
}
