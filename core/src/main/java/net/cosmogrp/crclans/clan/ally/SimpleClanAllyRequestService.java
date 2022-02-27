package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.enemy.ClanEnemyData;
import net.cosmogrp.crclans.clan.enemy.ClanEnemyService;
import net.cosmogrp.crclans.clan.service.AbstractClanService;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.time.TimeStamp;
import net.cosmogrp.crclans.user.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class SimpleClanAllyRequestService
        extends AbstractClanService<ClanAllyRequestData>
        implements ClanAllyRequestService {

    @Inject private ClanMemberService memberService;
    @Inject private ClanAllyService allyService;
    @Inject private ClanEnemyService enemyService;

    @Inject private FileConfiguration configuration;
    @Inject private GlobalNotifier globalNotifier;

    public SimpleClanAllyRequestService() {
        super(ClanAllyRequestData::create);
    }

    @Override
    public void sendAllyRequest(
            Player player, User user,
            String target
    ) {
        memberService.computeAsOwner(
                player, user,
                memberData -> {
                    String tag = memberData.getId();

                    if (tag.equals(target)) {
                        messageHandler.send(player, "clan.ally-request-self");
                        return;
                    }

                    ClanAllyData allyData = allyService.getData(player, tag);

                    if (allyData == null) {
                        return;
                    }

                    if (allyData.contains(target)) {
                        messageHandler.send(player, "clan.already-ally");
                        return;
                    }

                    ClanAllyRequestData requestData = getData(player, tag);

                    if (requestData == null) {
                        return;
                    }

                    TimeStamp timeStamp = requestData.getTimestamp(target);

                    if (timeStamp != null) {
                        if (timeStamp.hasExpired()) {
                            requestData.unbind(target);
                        } else {
                            messageHandler.send(
                                    player,
                                    "clan.already-ally-request"
                            );
                            return;
                        }
                    }

                    ClanMemberData targetMemberData = memberService.getData(
                            player, target
                    );

                    if (targetMemberData == null) {
                        return;
                    }

                    ClanMember owner = targetMemberData.getOwner();

                    if (!owner.isOnline()) {
                        messageHandler.send(
                                player,
                                "clan.ally-request-offline"
                        );
                        return;
                    }

                    long time = configuration.getLong(
                            "clans.ally-request-expiry",
                            600
                    );

                    requestData.bind(target, time);

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.ally-request-members",
                            "%tag%", tag
                    );

                    globalNotifier.singleNotifyIn(
                            owner.getPlayerId(),
                            "minimessage",
                            "clan.ally-request-target",
                            "%tag%", tag,
                            "%time%", time
                    );

                    save(player, requestData);
                }
        );
    }

    @Override
    public void acceptAlly(
            Player player, User user,
            ClanAllyRequestData requestData
    ) {
        memberService.computeAsOwner(
                player, user,
                targetMemberData -> {
                    String targetTag = targetMemberData.getId();
                    TimeStamp timeStamp = checkRequest(
                            requestData, targetTag, player
                    );

                    if (timeStamp == null) {
                        return;
                    }

                    String senderTag = requestData.getId();

                    ClanMemberData senderMemberData = memberService.getData(
                            player, senderTag
                    );

                    if (senderMemberData == null) {
                        return;
                    }

                    requestData.unbind(targetTag);

                    globalNotifier.notify(
                            targetMemberData.getOnlineIdMembers(),
                            "clan.ally-accept-target-members",
                            "%tag%", senderTag
                    );

                    globalNotifier.notify(
                            senderMemberData.getOnlineIdMembers(),
                            "clan.ally-accept-members",
                            "%tag%", targetTag
                    );

                    ClanAllyData clanAllyData = allyService.getData(senderTag);
                    ClanAllyData targetAllyData = allyService.getData(targetTag);

                    if (clanAllyData == null || targetAllyData == null) {
                        return;
                    }

                    clanAllyData.add(targetTag);
                    targetAllyData.add(senderTag);

                    removeEnemy(senderTag, targetTag);
                    removeEnemy(targetTag, senderTag);

                    save(player, requestData);
                    allyService.save(player, clanAllyData);
                    allyService.save(player, targetAllyData);
                }
        );
    }

    @Override
    public void denyAlly(
            Player player, User user,
            ClanAllyRequestData requestData
    ) {
        memberService.computeAsOwner(
                player, user,
                targetMemberData -> {
                    String targetTag = targetMemberData.getId();
                    TimeStamp timeStamp = checkRequest(
                            requestData, targetTag, player
                    );

                    if (timeStamp == null) {
                        return;
                    }

                    String senderTag = requestData.getId();
                    ClanMemberData senderMemberData = memberService.getData(
                            player, senderTag
                    );

                    if (senderMemberData == null) {
                        return;
                    }

                    requestData.unbind(targetTag);
                    globalNotifier.notify(
                            senderMemberData.getOnlineIdMembers(),
                            "clan.ally-deny-members",
                            "%tag%", targetTag
                    );

                    globalNotifier.notify(
                            targetMemberData.getOnlineIdMembers(),
                            "clan.ally-deny-target-members",
                            "%tag%", senderTag
                    );

                    save(player, requestData);
                }
        );
    }

    private @Nullable TimeStamp checkRequest(
            ClanAllyRequestData requestData,
            String tag, Player player
    ) {
        TimeStamp timeStamp = requestData.getTimestamp(tag);

        if (timeStamp == null) {
            messageHandler.send(player, "clan.ally-no-request");
            return null;
        }

        if (timeStamp.hasExpired()) {
            requestData.unbind(tag);
            messageHandler.send(player, "clan.ally-request-expired");
            save(player, requestData);
            return null;
        }

        return timeStamp;
    }

    private void removeEnemy(String targetTag, String senderTag) {
        ClanEnemyData targetEnemyData = enemyService
                .getData(targetTag);

        if (targetEnemyData != null) {
            if (targetEnemyData.remove(senderTag)) {
                enemyService.save(targetEnemyData);
            }
        }
    }
}
