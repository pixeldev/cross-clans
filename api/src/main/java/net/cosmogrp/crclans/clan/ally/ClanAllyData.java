package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.domain.Domain;
import net.cosmogrp.crclans.domain.SetDomain;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;

import java.util.Collection;

public class ClanAllyData extends AbstractModel
        implements DocumentCodec, Domain {

    private final Domain data;

    private ClanAllyData(String id, Domain data) {
        super(id);
        this.data = data;
    }

    @Override
    public Collection<String> getAll() {
        return data.getAll();
    }

    @Override
    public boolean add(String id) {
        return data.add(id);
    }

    @Override
    public boolean remove(String id) {
        return data.remove(id);
    }

    @Override
    public boolean contains(String id) {
        return data.contains(id);
    }

    public static ClanAllyData create(String tag) {
        return new ClanAllyData(tag, SetDomain.create());
    }

    public static ClanAllyData fromDocument(DocumentReader reader) {
        return new ClanAllyData(
                reader.readString("_id"),
                reader.readChild("allies", SetDomain::fromDocument)
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("allies", data)
                .build();
    }
}
