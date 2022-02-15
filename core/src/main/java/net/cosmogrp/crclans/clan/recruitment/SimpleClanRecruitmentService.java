package net.cosmogrp.crclans.clan.recruitment;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;

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
        clanUserService.executeAsOwner(
                sender, user,
                clan -> {
                    RecruitmentRequest request =
                            clan.getRequest(target.getPlayerId());

                    if (request != null) {
                        if (request.isExpired()) {
                            clan.removeRequest(request);
                        } else {
                            messageHandler.send(sender, "clan.already-invited");
                            return;
                        }
                    }

                    request = RecruitmentRequest.create(
                            target,
                            configuration.getInt("clans.invite-expiry")
                    );

                    OfflinePlayer targetPlayer = target.asPlayer();

                    if (targetPlayer instanceof Player) {

                    }

                    clan.addRequest(request);
                }
        );
    }

    @Override
    public void notifyRecruitment(
            Player target,
            RecruitmentMessage message
    ) {
        String clanTag = message.getClanTag();
    }
}