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

public class Clan extends AbstractModel
        implements DocumentCodec {

    private final Date creation;
    private final ClanMember owner;
    private final Set<ClanMember> members;
    private final Set<String> allies;
    private final Set<String> enemies;

    private String description;

    private Clan(
            String id, Date creation,
            ClanMember owner, Set<ClanMember> members,
            Set<String> allies,
            Set<String> enemies,
            String description
    ) {
        super(id);
        this.creation = creation;
        this.owner = owner;
        this.members = members;
        this.allies = allies;
        this.enemies = enemies;
        this.description = description;
    }

    public ClanMember getOwner() {
        return owner;
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
                "ownerId=" + owner +
                ", name='" + description + '\'' +
                ", members=" + members +
                '}';
    }

    public static Clan create(Player owner, String tag) {
        return new Clan(
                tag, new Date(), ClanMember.fromPlayer(owner),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                null
        );
    }

    public static Clan fromDocument(Document document) {
        Set<ClanMember> members = new HashSet<>();

        for (Document memberDocument : document.getList(
                "members", Document.class
        )) {
            members.add(ClanMember.fromDocument(memberDocument));
        }

        return new Clan(
                document.getString("_id"),
                document.getDate("creation"),
                ClanMember.fromDocument(document.get("owner", Document.class)),
                members,
                new HashSet<>(document.getList("allies", String.class)),
                new HashSet<>(document.getList("enemies", String.class)),
                document.getString("description")
        );
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        document.put("_id", getId());
        document.put("creation", creation);
        document.put("owner", owner.toDocument());
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
