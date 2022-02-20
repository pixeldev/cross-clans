package net.cosmogrp.crclans.user.clan;

import java.util.Set;
import java.util.UUID;

public class ClanDisbandMessage {

    private final Set<UUID> onlineMembers;

    public ClanDisbandMessage(Set<UUID> onlineMembers) {
        this.onlineMembers = onlineMembers;
    }

    public Set<UUID> getOnlineMembers() {
        return onlineMembers;
    }
}
