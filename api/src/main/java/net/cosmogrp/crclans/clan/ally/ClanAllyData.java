package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

public class ClanAllyData extends AbstractModel
        implements DocumentCodec {

    private final Set<String> allies;

    public ClanAllyData(String id, Set<String> allies) {
        super(id);
        this.allies = allies;
    }

    public Iterable<String> getAllies() {
        return allies;
    }

    public void addAlly(String ally) {
        allies.add(ally);
    }

    public void removeAlly(String ally) {
        allies.remove(ally);
    }

    public boolean isAlly(String ally) {
        return allies.contains(ally);
    }

    public static ClanAllyData create(String tag) {
        return new ClanAllyData(tag, new HashSet<>());
    }

    public static ClanAllyData fromDocument(DocumentReader reader) {
        return new ClanAllyData(
                reader.readString("_id"),
                reader.readSet("allies", String.class)
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("allies", allies)
                .build();
    }
}
