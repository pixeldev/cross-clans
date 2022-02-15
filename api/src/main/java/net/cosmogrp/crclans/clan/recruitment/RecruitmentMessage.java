package net.cosmogrp.crclans.clan.recruitment;

public class RecruitmentMessage {

    private final String clanTag;
    private final long expiryTime;

    public RecruitmentMessage(String clanTag, long expiryTime) {
        this.clanTag = clanTag;
        this.expiryTime = expiryTime;
    }

    public String getClanTag() {
        return clanTag;
    }

    public long getExpiryTime() {
        return expiryTime;
    }
}
