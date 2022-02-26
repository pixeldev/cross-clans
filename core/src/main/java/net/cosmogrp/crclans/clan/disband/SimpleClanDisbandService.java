package net.cosmogrp.crclans.clan.disband;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.redis.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SimpleClanDisbandService
        implements ClanDisbandService {

    @Inject private Executor executor;

    @Inject private MessageHandler messageHandler;
    @Inject private GlobalNotifier globalNotifier;
    @Inject private LogHandler logHandler;

    @Inject private ClanMemberService memberService;
    @Inject private UserService userService;
    @Inject private ClanAllyService allyService;

    @Inject private Channel<ClanDisbandMessage> disbandChannel;

    private final Collection<ClanService<? extends Model>> services;

    @Inject
    public SimpleClanDisbandService(CrClansPlugin plugin) {
        this.services = plugin.getServices();
    }

    @Override
    public void disbandClan(Player player, User user) {
        memberService.computeAsOwner(
                player, user,
                memberData -> {
                    String tag = memberData.getId();

                    CompletableFuture.runAsync(() -> {
                                for (ClanService<?> service : services) {
                                    service.deleteSync(tag);
                                }
                            }, executor)
                            .whenComplete((result, error) -> {
                                if (error != null) {
                                    logHandler.reportError(
                                            "Failed to delete clan '%s'", error,
                                            tag
                                    );

                                    messageHandler.send(
                                            player,
                                            "clan.disband-failed"
                                    );
                                    return;
                                }

                                ClanAllyData allyData = allyService.getData(tag);

                                if (allyData == null) {
                                    return;
                                }

                                for (String ally : allyData.getAll()) {
                                    allyService.removeAlly(tag, ally);
                                }

                                user.setClan(null);
                                memberData.removeMember(user.getPlayerId());

                                messageHandler.send(player, "clan.disband-success");

                                Set<UUID> onlineMembers =
                                        memberData.getOnlineIdMembers();

                                if (notifyDisband(onlineMembers)) {
                                    return;
                                }

                                disbandChannel.sendMessage(new ClanDisbandMessage(onlineMembers));
                            });
                }
        );
    }

    @Override
    public boolean notifyDisband(Set<UUID> onlineMembers) {
        boolean success = true;

        for (UUID uuid : onlineMembers) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                success = false;
                continue;
            }

            User user = userService.getUser(uuid);

            if (user == null) {
                success = false;
                continue;
            }

            user.setClan(null);
            messageHandler.send(player, "clan.disband-members");
        }

        return success;
    }

    @Override
    public void leaveClan(Player player, User user) {
        String tag = memberService.getClanTag(player, user);

        if (tag == null) {
            return;
        }

        ClanMemberData memberData = memberService.getData(player, tag);

        if (memberData == null) {
            return;
        }

        if (memberData.isOwner(player)) {
            messageHandler.send(player, "clan.cannot-leave-own-clan");
            return;
        }

        ClanMember clanMember = memberData.removeMember(user.getPlayerId());

        if (clanMember == null) {
            messageHandler.send(player, "clan.error-leaving-clan");
            return;
        }

        user.setClan(null);

        messageHandler.sendReplacing(
                player, "clan.left-clan-sender",
                "%tag%", memberData.getId()
        );

        globalNotifier.notify(
                memberData.getOnlineIdMembers(),
                clanMember.isModerator() ?
                        "clan.moderator-left-clan-members"
                        : "clan.left-clan-members",
                "%target%", player.getName()
        );

        memberService.save(player, memberData);
    }

}
