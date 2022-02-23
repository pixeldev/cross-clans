package net.cosmogrp.crclans.clan.recruitment;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanRecruitmentData extends AbstractModel
        implements DocumentCodec {

    private final Map<UUID, RecruitmentRequest> requests;

    public ClanRecruitmentData(String id) {
        super(id);
        this.requests = new HashMap<>();
    }

    public @Nullable RecruitmentRequest getRequest(UUID playerId) {
        return requests.get(playerId);
    }

    public void addRequest(RecruitmentRequest request) {
        requests.put(request.getPlayerId(), request);
    }

    public void removeRequest(RecruitmentRequest request) {
        requests.remove(request.getPlayerId());
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("recruitments", requests)
                .build();
    }
}
