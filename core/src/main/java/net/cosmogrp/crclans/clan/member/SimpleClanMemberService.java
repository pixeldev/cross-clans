package net.cosmogrp.crclans.clan.member;

import net.cosmogrp.crclans.clan.service.AbstractClanService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import net.cosmogrp.storage.redis.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;
import java.util.function.Consumer;

public class SimpleClanMemberService
        extends AbstractClanService<ClanMemberData>
        implements ClanMemberService {

    @Inject private UserService userService;

    @Inject private GlobalNotifier globalNotifier;
    @Inject private ClusteredUserRegistry clusteredUserRegistry;

    @Inject private Channel<ClanKickMessage> kickChannel;

    public SimpleClanMemberService() {
        super(ClanMemberData::create);
    }

    @Override
    public void createSync(Player creator, String tag) {
        ClanMemberData memberData = ClanMemberData.create(tag);
        ClanMember owner = ClanMember.fromPlayer(creator);

        owner.setModerator(true);
        memberData.setOwner(owner);
        memberData.addMember(owner);

        modelService.saveSync(memberData);
    }

    @Override
    public @Nullable String getClanTag(Player player, User user) {
        String clanTag = user.getClanTag();

        if (clanTag == null) {
            messageHandler.send(player, "clan.not-in-clan");
        }

        return clanTag;
    }

    @Override
    public boolean checkModerator(Player player, ClanMemberData memberData) {
        ClanMember member = memberData.getMember(player.getUniqueId());

        if (member == null) {
            // this should never happen
            return false;
        }

        if (!member.isModerator()) {
            messageHandler.send(player, "clan.not-mod");
            return false;
        }

        return true;
    }

    @Override
    public void computeAsModerator(
            Player player, User user,
            Consumer<ClanMemberData> consumer
    ) {
        String clanTag = getClanTag(player, user);

        if (clanTag == null) {
            return;
        }

        ClanMemberData memberData = getData(player, clanTag);

        if (memberData == null) {
            return;
        }

        if (!checkModerator(player, memberData)) {
            return;
        }

        consumer.accept(memberData);
    }

    @Override
    public void transferOwner(
            Player player, User user,
            ClanMember target
    ) {
        computeAsOwner(player, user, data -> {
            data.setOwner(target);
            target.setModerator(true);

            globalNotifier.notify(
                    data.getOnlineIdMembers(),
                    "clan.transfer-owner-members",
                    "%target%", target.getPlayerName()
            );

            if (target.isOnline()) {
                globalNotifier.singleNotify(
                        target.getPlayerId(),
                        "clan.transfer-owner-target"
                );
            }

            save(player, data);
        });
    }

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
        if (!checkOwner(player, data)) {
            return;
        }

        if (target.isModerator()) {
            messageHandler.send(player, "clan.already-mod");
            return;
        }

        target.setModerator(true);

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

    @Override
    public void demote(
            Player player, User user,
            ClanMemberData data, ClanMember target
    ) {
        if (!checkOwner(player, data)) {
            return;
        }

        if (data.isOwner(target.getPlayerId())) {
            messageHandler.send(player, "clan.cannot-demote-owner");
            return;
        }

        if (!target.isModerator()) {
            messageHandler.send(player, "clan.demote-not-mod");
            return;
        }

        target.setModerator(false);

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

    @Override
    public void computeAsOwner(
            Player player, User user,
            Consumer<ClanMemberData> action
    ) {
        String tag = user.getClanTag();

        if (tag == null) {
            messageHandler.send(player, "clan.not-in-clan");
            return;
        }

        ClanMemberData memberData = getData(player, tag);

        if (memberData == null) {
            return;
        }

        if (checkOwner(player, memberData)) {
            action.accept(memberData);
        }
    }

    private boolean checkOwner(
            Player player, ClanMemberData memberData
    ) {
        if (!memberData.isOwner(player)) {
            messageHandler.send(player, "clan.not-owner");
            return false;
        }

        return true;
    }
}
