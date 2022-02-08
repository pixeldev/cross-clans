package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.mongo.MongoModelParser;
import org.bson.Document;

public class ClanModelParser implements MongoModelParser<Clan> {
    @Override
    public Clan parse(Document document) {
        return null;
    }
}
