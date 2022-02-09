package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Clan extends AbstractModel
        implements DocumentCodec {

    private final Date creation;
    private final UUID ownerId;
    private final Set<ClanMember> members;
    private final Set<String> allies;
    private final Set<String> enemies;

    private String description;

    private Clan(
            String id, Date creation,
            UUID ownerId, Set<ClanMember> members,
            Set<String> allies,
            Set<String> enemies,
            String description
    ) {
        super(id);
        this.creation = creation;
        this.ownerId = ownerId;
        this.members = members;
        this.allies = allies;
        this.enemies = enemies;
        this.description = description;
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

    public static Clan create(Player owner, String tag) {
        return new Clan(
                tag, new Date(), owner.getUniqueId(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                null
        );
    }

    public static Clan fromDocument(Document document) {
        return new Clan(
                document.getString("_id"),
                document.getDate("creation"),
                document.get("ownerId", UUID.class),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                document.getString("description")
        );
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        document.put("_id", getId());
        document.put("creation", creation);
        document.put("ownerId", ownerId.toString());
        document.put("description", description);

        List<Document> members = new ArrayList<>();

        for (ClanMember member : this.members) {
            members.add(member.toDocument());
        }

        document.put("members", members);
        document.put("allies", allies);
        document.put("enemies", enemies);
        return document;
    }
}
