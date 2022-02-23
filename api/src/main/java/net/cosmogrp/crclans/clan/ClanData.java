package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;

public class ClanData extends AbstractModel
        implements DocumentCodec {

    private final Date creation;
    private ClanMember owner;
    private String description;

    public ClanData(
            String id, Date creation,
            String description, ClanMember owner
    ) {
        super(id);
        this.creation = creation;
        this.description = description;
        this.owner = owner;
    }

    public Date getCreation() {
        return creation;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClanMember getOwner() {
        return owner;
    }

    public void setOwner(ClanMember owner) {
        if (owner == null) {
            return;
        }

        this.owner = owner;
    }

    public boolean isOwner(UUID playerId) {
        return owner.getPlayerId()
                .equals(playerId);
    }

    public boolean isOwner(OfflinePlayer player) {
        return isOwner(player.getUniqueId());
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("creation", creation)
                .write("description", description)
                .write("owner", owner)
                .build();
    }
}
