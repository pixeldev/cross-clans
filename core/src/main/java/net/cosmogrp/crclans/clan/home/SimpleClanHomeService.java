package net.cosmogrp.crclans.clan.home;

import net.cosmogrp.crclans.clan.service.AbstractClanService;
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

    @Inject private ClanMemberService memberService;

    public SimpleClanHomeService() {
        super(ClanHomeData::create);
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

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.set-home-members"
                    );

                    save(player, homeData);
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

                    homeData.setHome(null);

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.del-home-members"
                    );

                    save(player, homeData);
                });
    }

    @Override
    public void teleportToHome(Player player, User user) {
        String tag = memberService.getClanTag(player, user);

        if (tag == null) {
            return;
        }

        ClanHomeData homeData = getData(player, tag);

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
