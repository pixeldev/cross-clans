package net.cosmogrp.crclans.clan.member;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
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

    private ClanMemberData(
            String id,
            Map<UUID, ClanMember> members
    ) {
        super(id);
        this.members = members;
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

    public static ClanMemberData create(String tag) {
        return new ClanMemberData(tag, new HashMap<>());
    }

    public static ClanMemberData fromDocument(DocumentReader reader) {
        return new ClanMemberData(
                reader.readString("_id"),
                reader.readMap(
                        "members",
                        ClanMember::getPlayerId,
                        ClanMember::fromDocument
                )
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("members", members)
                .build();
    }
}
