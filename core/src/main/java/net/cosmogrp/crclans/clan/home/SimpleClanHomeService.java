package net.cosmogrp.crclans.clan.home;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.server.ServerData;
import net.cosmogrp.crclans.server.ServerLocation;
import net.cosmogrp.crclans.server.ServerSender;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanHomeService
        implements ClanHomeService {

    @Inject private MessageHandler messageHandler;
    @Inject private GlobalNotifier globalNotifier;

    @Inject private ClanUserService clanUserService;

    @Inject private ServerSender serverSender;
    @Inject private ServerData serverData;

    @Override
    public void setHome(Player player, User user) {
        clanUserService.computeAsOwner(player, user, clan -> {
            clan.setHome(ServerLocation.centered(serverData, player));

            messageHandler.send(player, "clan.set-home-sender");

            globalNotifier.notify(
                    clan.getOnlineMembers(),
                    "clan.set-home-members"
            );
        });
    }

    @Override
    public void delHome(Player player, User user) {
        clanUserService.computeAsOwner(player, user, clan -> {
            clan.setHome(null);

            messageHandler.send(player, "clan.del-home-sender");

            globalNotifier.notify(
                    clan.getOnlineMembers(),
                    "clan.del-home-members"
            );
        });
    }

    @Override
    public void teleportToHome(Player player, User user) {
        Clan clan = clanUserService.getClan(player, user);

        if (clan == null) {
            return;
        }

        ServerLocation home = clan.getHome();

        if (home == null) {
            messageHandler.send(player, "clan.no-home");
            return;
        }

        messageHandler.send(player, "clan.teleport-home");
        serverSender.teleport(player, home);
    }
}
