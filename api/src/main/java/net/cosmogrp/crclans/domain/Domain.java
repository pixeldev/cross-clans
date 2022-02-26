package net.cosmogrp.crclans.domain;

import net.cosmogrp.storage.mongo.DocumentCodec;

import java.util.Collection;

public interface Domain extends DocumentCodec {

    Collection<String> getAll();

    boolean add(String id);

    boolean remove(String id);

    boolean contains(String id);

}
