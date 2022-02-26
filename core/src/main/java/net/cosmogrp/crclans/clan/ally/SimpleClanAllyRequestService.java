package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.AbstractClanService;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.time.TimeStamp;
import net.cosmogrp.crclans.user.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanAllyRequestService
        extends AbstractClanService<ClanAllyRequestData>
        implements ClanAllyRequestService {

    @Inject private ClanMemberService memberService;
    @Inject private ClanAllyService allyService;

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

                    if (allyData.isAlly(target)) {
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
                    messageHandler.sendReplacing(
                            player, "clan.ally-request-sender",
                            "%tag%", tag
                    );

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.ally-request-members",
                            "%tag%", tag
                    );

                    globalNotifier.singleNotifyIn(
                            owner.getPlayerId(),
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
            Player player, String target,
            ClanAllyRequestData data
    ) {

    }

    @Override
    public void denyAlly(
            Player player, String target,
            ClanAllyRequestData data
    ) {

    }
}
