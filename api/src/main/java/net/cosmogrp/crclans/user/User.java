package net.cosmogrp.crclans.user;

import net.cosmogrp.storage.model.AbstractModel;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User extends AbstractModel {

    private final UUID playerId;

    public User(Player player) {
        super(player.getUniqueId().toString());
        this.playerId = player.getUniqueId();
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
