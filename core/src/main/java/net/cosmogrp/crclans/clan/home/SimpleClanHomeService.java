package net.cosmogrp.crclans.clan.home;

import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.AbstractClanService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.server.ServerData;
import net.cosmogrp.crclans.server.ServerLocation;
import net.cosmogrp.crclans.server.ServerSender;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanHomeService
        extends AbstractClanService<ClanHomeData>
        implements ClanHomeService {

    @Inject private GlobalNotifier globalNotifier;

    @Inject private ServerSender serverSender;
    @Inject private ServerData serverData;

    private final ClanMemberService memberService;

    @Inject
    public SimpleClanHomeService(CrClansPlugin plugin) {
        super(ClanHomeData::create);
        this.memberService = (ClanMemberService)
                plugin.getService(ClanMemberData.class);
    }

    @Override
    public void setHome(Player player, User user) {
        memberService.computeAsOwner(
                player, user,
                memberData -> {
                    ClanHomeData homeData = getData(player, memberData.getId());

                    if (homeData == null) {
                        return;
                    }

                    homeData.setHome(ServerLocation.centered(
                            serverData,
                            player
                    ));

                    messageHandler.send(player, "clan.set-home-sender");

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.set-home-members"
                    );
                }
        );
    }

    @Override
    public void delHome(Player player, User user) {
        memberService.computeAsOwner(
                player, user,
                memberData -> {
                    ClanHomeData homeData = getData(player, memberData.getId());

                    if (homeData == null) {
                        return;
                    }

                    homeData.setHome(ServerLocation.centered(
                            serverData,
                            player
                    ));

                    messageHandler.send(player, "clan.del-home-sender");

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.del-home-members"
                    );
                });
    }

    @Override
    public void teleportToHome(Player player, User user) {
        ClanHomeData homeData = getData(player, user.getId());

        if (homeData == null) {
            return;
        }

        ServerLocation home = homeData.getHome();

        if (home == null) {
            messageHandler.send(player, "clan.no-home");
            return;
        }

        messageHandler.send(player, "clan.teleport-home");
        serverSender.teleport(player, home);
    }
}
