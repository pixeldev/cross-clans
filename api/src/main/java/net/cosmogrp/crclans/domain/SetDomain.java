package net.cosmogrp.crclans.domain;

import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetDomain implements Domain {

    private final Set<String> data;

    private SetDomain(Set<String> data) {
        this.data = data;
    }

    @Override
    public Collection<String> getAll() {
        return data;
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

    public static SetDomain create() {
        return new SetDomain(new HashSet<>());
    }

    public static SetDomain fromDocument(DocumentReader reader) {
        return new SetDomain(reader.readSet("value", String.class));
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("value", data)
                .build();
    }
}
