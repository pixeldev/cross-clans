package net.cosmogrp.crclans.clan.member;

import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClanMemberData extends AbstractModel
        implements DocumentCodec {

    private final Map<UUID, ClanMember> members;

    public ClanMemberData(String id) {
        super(id);
        this.members = new HashMap<>();
    }

    public Collection<ClanMember> getMembers() {
        return members.values();
    }

    public Set<UUID> getOnlineIdMembers() {
        return members.values().stream()
                .filter(ClanMember::isOnline)
                .map(ClanMember::getPlayerId)
                .collect(Collectors.toSet());
    }

    public Set<ClanMember> getOnlineMembers() {
        return members.values().stream()
                .filter(ClanMember::isOnline)
                .collect(Collectors.toSet());
    }

    public @Nullable ClanMember getMember(UUID uuid) {
        return members.get(uuid);
    }

    public boolean isMember(UUID uuid) {
        return members.containsKey(uuid);
    }

    public @Nullable ClanMember removeMember(UUID uuid) {
        return members.remove(uuid);
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("members", members)
                .build();
    }
}
