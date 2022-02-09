package net.cosmogrp.crclans.user;

import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.storage.model.AbstractModel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class User extends AbstractModel {

    private final UUID playerId;

    private String clanTag;

    public User(Player player) {
        super(player.getUniqueId().toString());
        this.playerId = player.getUniqueId();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setClan(@Nullable Clan clan) {
        if (clan == null) {
            this.clanTag = null;
        } else {
            this.clanTag = clan.getId();
        }
    }

    public @Nullable String getClanTag() {
        return clanTag;
    }

    public boolean hasClan() {
        return clanTag != null;
    }
}
