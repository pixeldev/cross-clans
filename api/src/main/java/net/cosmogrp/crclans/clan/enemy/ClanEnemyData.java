package net.cosmogrp.crclans.clan.enemy;

import net.cosmogrp.crclans.domain.Domain;
import net.cosmogrp.crclans.domain.SetDomain;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;

import java.util.Collection;

public class ClanEnemyData extends AbstractModel
        implements DocumentCodec, Domain {

    private final Domain domain;

    private ClanEnemyData(String id, Domain domain) {
        super(id);
        this.domain = domain;
    }

    @Override
    public Collection<String> getAll() {
        return domain.getAll();
    }

    @Override
    public boolean add(String id) {
        return domain.add(id);
    }

    @Override
    public boolean remove(String id) {
        return domain.remove(id);
    }

    @Override
    public boolean contains(String id) {
        return domain.contains(id);
    }

    public static ClanEnemyData create(String tag) {
        return new ClanEnemyData(tag, SetDomain.create());
    }

    public static ClanEnemyData fromDocument(DocumentReader reader) {
        return new ClanEnemyData(
                reader.readString("_id"),
                reader.readChild("domain", SetDomain::fromDocument)
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("domain", domain)
                .build();
    }
}
