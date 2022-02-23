package net.cosmogrp.crclans.clan.member;

import net.cosmogrp.crclans.clan.AbstractClanService;
import net.cosmogrp.crclans.clan.ClanDataService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.storage.redis.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleClanMemberService
        extends AbstractClanService<ClanMemberData>
        implements ClanMemberService {

    @Inject private ClanDataService dataService;
    @Inject private UserService userService;

    @Inject private GlobalNotifier globalNotifier;
    @Inject private ClusteredUserRegistry clusteredUserRegistry;

    @Inject private Channel<ClanKickMessage> kickChannel;

    @Override
    public void kick(
            Player player, User user,
            ClanMemberData data, ClanMember target
    ) {
        ClanMember clanMember = data.getMember(user.getPlayerId());

        if (clanMember == null) {
            // this should never happen
            return;
        }

        if (!clanMember.isModerator()) {
            messageHandler.send(player, "clan.not-mod");
            return;
        }

        if (target.isModerator()) {
            messageHandler.send(player, "clan.kick-mod");
            return;
        }

        UUID targetId = target.getPlayerId();
        String targetName = target.getPlayerName();

        data.removeMember(targetId);
        messageHandler.sendReplacing(
                player, "clan.kick-success-sender",
                "%target%", targetName
        );

        globalNotifier.notify(
                data.getOnlineIdMembers(),
                "clan.kick-success-members",
                "%sender%", player.getName(),
                "%target%", targetName
        );

        save(player, data);

        ClusteredUser clusteredTarget =
                clusteredUserRegistry.find(targetName);

        if (clusteredTarget != null) {
            String clanTag = data.getId();
            if (notifyKick(targetId, clanTag)) {
                return;
            }

            kickChannel.sendMessage(
                    new ClanKickMessage(targetId, clanTag),
                    clusteredTarget.getServerData().getRedisServer()
            );
        }
    }

    @Override
    public void promote(
            Player player, User user,
            ClanMemberData data, ClanMember target
    ) {
        dataService.computeAsOwner(
                player, user,
                clanData -> {
                    if (target.isModerator()) {
                        messageHandler.send(player, "clan.already-mod");
                        return;
                    }

                    target.setModerator(true);
                    messageHandler.sendReplacing(
                            player, "clan.promote-success-sender",
                            "%target%", target.getPlayerName()
                    );

                    globalNotifier.notify(
                            data.getOnlineIdMembers(),
                            "clan.promote-success-members",
                            "%sender%", player.getName(),
                            "%target%", target.getPlayerName()
                    );

                    globalNotifier.singleNotify(
                            target.getPlayerId(),
                            "clan.promote-success-target",
                            "%sender%", player.getName()
                    );

                    save(player, data);
                }
        );
    }

    @Override
    public void demote(
            Player player, User user,
            ClanMemberData data, ClanMember target
    ) {
        dataService.computeAsOwner(
                player, user,
                clanData -> {
                    if (clanData.isOwner(target.getPlayerId())) {
                        messageHandler.send(player, "clan.cannot-demote-owner");
                        return;
                    }

                    if (!target.isModerator()) {
                        messageHandler.send(player, "clan.demote-not-mod");
                        return;
                    }

                    target.setModerator(false);
                    messageHandler.sendReplacing(
                            player, "clan.demote-success-sender",
                            "%target%", target.getPlayerName()
                    );

                    globalNotifier.notify(
                            data.getOnlineIdMembers(),
                            "clan.demote-success-members",
                            "%sender%", player.getName(),
                            "%target%", target.getPlayerName()
                    );

                    globalNotifier.singleNotify(
                            target.getPlayerId(),
                            "clan.demote-success-target",
                            "%sender%", player.getName()
                    );

                    save(player, data);
                }
        );
    }

    @Override
    public boolean notifyKick(UUID targetId, String clanId) {
        Player player = Bukkit.getPlayer(targetId);

        if (player == null) {
            return false;
        }

        User user = userService.getUser(player);

        if (user == null) {
            return false;
        }

        user.setClan(null);
        messageHandler.sendReplacing(
                player, "clan.kick-success-target",
                "%tag%", clanId
        );

        return true;
    }
}
