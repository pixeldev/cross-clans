package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.AbstractModel;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Clan extends AbstractModel {

    private final UUID ownerId;
    private final Set<UUID> members;
    private String description;

    public Clan(String tag , Player owner) {
        super(tag);
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

    public void setDescription(String description) {
        this.description = description;
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
