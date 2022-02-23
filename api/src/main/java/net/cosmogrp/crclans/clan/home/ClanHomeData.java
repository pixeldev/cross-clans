package net.cosmogrp.crclans.clan.home;

import net.cosmogrp.crclans.server.ServerLocation;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

public class ClanHomeData extends AbstractModel
        implements DocumentCodec {

    private ServerLocation home;

    public ClanHomeData(String id) {
        super(id);
    }

    public @Nullable ServerLocation getHome() {
        return home;
    }

    public void setHome(ServerLocation home) {
        this.home = home;
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("home", home)
                .build();
    }
}
