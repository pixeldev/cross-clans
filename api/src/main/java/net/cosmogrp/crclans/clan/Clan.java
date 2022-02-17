package net.cosmogrp.crclans.clan;

import net.cosmogrp.crclans.clan.recruitment.RecruitmentRequest;
import net.cosmogrp.crclans.server.ServerLocation;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Clan extends AbstractModel
        implements DocumentCodec {

    private final Date creation;
    private final ClanMember owner;
    private final Map<UUID, ClanMember> members;
    private final Set<String> allies;
    private final Set<String> enemies;
    private final Map<UUID, RecruitmentRequest> recruitmentRequests;

    private String description;
    private ServerLocation homeLocation;

    private Clan(
            String id, Date creation,
            ClanMember owner, Map<UUID, ClanMember> members,
            Set<String> allies,
            Set<String> enemies,
            Map<UUID, RecruitmentRequest> recruitmentRequests,
            String description, ServerLocation homeLocation
    ) {
        super(id);
        this.creation = creation;
        this.owner = owner;
        this.members = members;
        this.allies = allies;
        this.enemies = enemies;
        this.recruitmentRequests = recruitmentRequests;
        this.description = description;
        this.homeLocation = homeLocation;

        owner.setModerator(true);
        members.put(owner.getPlayerId(), owner);
    }

    public ClanMember getOwner() {
        return owner;
    }

    public boolean isOwner(Player player) {
        return owner.getPlayerId().equals(player.getUniqueId());
    }

    public @Nullable ServerLocation getHome() {
        return homeLocation;
    }

    public void setHome(ServerLocation homeLocation) {
        this.homeLocation = homeLocation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public @Nullable RecruitmentRequest getRequest(UUID playerId) {
        return recruitmentRequests.get(playerId);
    }

    public void addRequest(RecruitmentRequest request) {
        recruitmentRequests.put(request.getPlayerId(), request);
    }

    public void removeRequest(RecruitmentRequest request) {
        recruitmentRequests.remove(request.getPlayerId());
    }

    public Set<UUID> getOnlineMembers() {
        Set<UUID> onlineMembers = new HashSet<>();

        for (ClanMember member : members.values()) {
            if (member.isOnline()) {
                onlineMembers.add(member.getPlayerId());
            }
        }

        return onlineMembers;
    }

    public Collection<ClanMember> getMembers() {
        return members.values();
    }

    public @Nullable ClanMember getMember(UUID playerId) {
        return members.get(playerId);
    }

    public void addMember(Player member) {
        members.put(
                member.getUniqueId(),
                ClanMember.fromPlayer(member)
        );
    }

    public boolean isMember(UUID playerId) {
        return members.containsKey(playerId);
    }

    public ClanMember removeMember(UUID playerId) {
        return members.remove(playerId);
    }

    @Override
    public String toString() {
        return "Clan{" +
                "ownerId=" + owner +
                ", name='" + description + '\'' +
                ", members=" + members +
                '}';
    }

    public static Clan create(Player owner, String tag) {
        return new Clan(
                tag, new Date(), ClanMember.fromPlayer(owner),
                new HashMap<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashMap<>(),
                null, null
        );
    }

    public static Clan fromDocument(DocumentReader reader) {
        return new Clan(
                reader.readString("_id"),
                reader.readDate("creation"),
                reader.readChild("owner", ClanMember::fromDocument),
                reader.readMap(
                        "members",
                        ClanMember::getPlayerId,
                        ClanMember::fromDocument
                ),
                reader.readSet("allies", String.class),
                reader.readSet("enemies", String.class),
                reader.readMap(
                        "recruitments",
                        RecruitmentRequest::getPlayerId,
                        RecruitmentRequest::fromDocument
                ),
                reader.readString("description"),
                reader.readChild("home", ServerLocation::fromDocument)
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("creation", creation)
                .write("owner", owner)
                .write("description", description)
                .write("members", members)
                .write("allies", allies)
                .write("enemies", enemies)
                .write("recruitments", recruitmentRequests)
                .write("home", homeLocation)
                .build();
    }
}
