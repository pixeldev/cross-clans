package net.cosmogrp.crclans.clan.recruitment;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleClanRecruitmentService
        implements ClanRecruitmentService {

    @Inject private GlobalNotifier globalNotifier;
    @Inject private ClanUserService clanUserService;
    @Inject private MessageHandler messageHandler;
    @Inject private FileConfiguration configuration;

    @Override
    public void sendRecruitment(
            Player sender, User user,
            ClusteredUser target
    ) {
        clanUserService.computeAsOwner(
                sender, user,
                clan -> {
                    UUID targetId = target.getPlayerId();

                    if (clan.isMember(targetId)) {
                        messageHandler.send(
                                sender, "clan.already-member"
                        );
                        return;
                    }

                    RecruitmentRequest request =
                            clan.getRequest(targetId);

                    if (request != null) {
                        if (request.isExpired()) {
                            clan.removeRequest(request);
                        } else {
                            messageHandler.send(sender, "clan.already-invited");
                            return;
                        }
                    }

                    int time = configuration.getInt("clans.invite-expiry");
                    request = RecruitmentRequest.create(
                            target, time
                    );

                    clan.addRequest(request);
                    messageHandler.sendReplacing(
                            sender, "clan.invited-sender",
                            "%target%", target.asPlayer().getName()
                    );

                    globalNotifier.singleNotifyIn(
                            target.getPlayerId(), "minimessage",
                            "clan.invited-target",
                            "%tag%", clan.getId(),
                            "%time%", time
                    );
                }
        );
    }

}
