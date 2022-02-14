package net.cosmogrp.crclans.clan.recruitment;

import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;

import java.util.Objects;
import java.util.UUID;

public class RecruitmentRequest implements DocumentCodec {

    private final UUID playerId;
    private final long expiration;

    private RecruitmentRequest(UUID playerId, long expiration) {
        this.playerId = playerId;
        this.expiration = expiration;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getExpiration() {
        return expiration;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecruitmentRequest that = (RecruitmentRequest) o;
        return expiration == that.expiration && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public String toString() {
        return "RecruitmentRequest{" +
                "playerId=" + playerId +
                ", expiration=" + expiration +
                '}';
    }

    public static RecruitmentRequest dummy(UUID playerId) {
        return new RecruitmentRequest(playerId, -1);
    }

    public static RecruitmentRequest create(
            ClusteredUser user,
            long expirationInSeconds
    ) {
        return new RecruitmentRequest(
                user.getPlayerId(),
                System.currentTimeMillis() + (expirationInSeconds * 1000)
        );
    }

    public static RecruitmentRequest fromDocument(DocumentReader reader) {
        return new RecruitmentRequest(
                reader.readUuid("playerId"),
                reader.readLong("expiration")
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("playerId", playerId)
                .write("expiration", expiration)
                .build();
    }
}
