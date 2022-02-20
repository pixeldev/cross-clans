package net.cosmogrp.crclans.clan.mod;

import java.util.UUID;

public class ClanKickMessage {

    private final UUID target;
    private final String clanId;

    public ClanKickMessage(UUID target, String clanId) {
        this.target = target;
        this.clanId = clanId;
    }

    public UUID getTarget() {
        return target;
    }

    public String getClanId() {
        return clanId;
    }
}
