package net.cosmogrp.crclans.clan.recruitment;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanRecruitmentData extends AbstractModel
        implements DocumentCodec {

    private final Map<UUID, RecruitmentRequest> requests;

    private ClanRecruitmentData(
            String id,
            Map<UUID, RecruitmentRequest> requests
    ) {
        super(id);
        this.requests = requests;
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

    public static ClanRecruitmentData create(String tag) {
        return new ClanRecruitmentData(tag, new HashMap<>());
    }

    public static ClanRecruitmentData fromDocument(DocumentReader reader) {
        return new ClanRecruitmentData(
                reader.readString("_id"),
                reader.readMap(
                        "recruitments",
                        RecruitmentRequest::getPlayerId,
                        RecruitmentRequest::fromDocument
                )
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create(this)
                .write("recruitments", requests)
                .build();
    }
}
