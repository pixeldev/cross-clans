package net.cosmogrp.crclans.clan.home;

import net.cosmogrp.crclans.server.ServerLocation;
import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

public class ClanHomeData extends AbstractModel
        implements DocumentCodec {

    private ServerLocation home;

    private ClanHomeData(String id, ServerLocation home) {
        super(id);
        this.home = home;
    }

    public @Nullable ServerLocation getHome() {
        return home;
    }

    public void setHome(ServerLocation home) {
        this.home = home;
    }

    public static ClanHomeData create(String tag) {
        return new ClanHomeData(tag, null);
    }

    public static ClanHomeData fromDocument(DocumentReader reader) {
        return new ClanHomeData(
                reader.readString("_id"),
                reader.readChild("home", ServerLocation::fromDocument)
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("home", home)
                .build();
    }
}
