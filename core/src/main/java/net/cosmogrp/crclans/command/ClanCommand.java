package net.cosmogrp.crclans.command;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.clan.disband.ClanDisbandService;
import net.cosmogrp.crclans.clan.home.ClanHomeService;
import net.cosmogrp.crclans.clan.mod.ClanModerationService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.command.part.ClanPart;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = {"clan", "clans"})
public class ClanCommand implements CommandClass {

    @Inject private ClanService clanService;
    @Inject private ClanRecruitmentService recruitmentService;
    @Inject private ClanHomeService clanHomeService;
    @Inject private ClanDisbandService clanDisbandService;
    @Inject private ClanModerationService clanModerationService;

    @Command(names = "create", permission = "clans.create")
    public void create(@Sender Player sender, @Sender User user, String tag) {
        clanService.createClan(user, sender, tag);
    }

    @Command(names = "sethome", permission = "clans.sethome")
    public void runSetHome(@Sender Player sender, @Sender User user) {
        clanHomeService.setHome(sender, user);
    }

    @Command(names = "delhome", permission = "clans.delhome")
    public void runDelHome(@Sender Player sender, @Sender User user) {
        clanHomeService.delHome(sender, user);
    }

    @Command(names = "home", permission = "clans.home")
    public void runHome(@Sender Player sender, @Sender User user) {
        clanHomeService.teleportToHome(sender, user);
    }

    @Command(names = "disband", permission = "clans.disband")
    public void runDelete(@Sender Player sender, @Sender User user) {
        clanDisbandService.disbandClan(sender, user);
    }

    @Command(names = "kick", permission = "clans.kick")
    public void runKick(
            CommandContext commandContext,
            @Sender Player sender, @Sender User user,
            ClanMember clanMember
    ) {
        clanModerationService.kickMember(
                sender, user,
                commandContext.getObject(Clan.class, ClanPart.CLAN_CONTEXT_KEY),
                clanMember
        );
    }

    @Command(names = "promote", permission = "clans.promote")
    public void runPromote(
            CommandContext commandContext,
            @Sender Player sender, @Sender User user,
            ClanMember clanMember
    ) {
        clanModerationService.promoteMember(
                sender, user,
                commandContext.getObject(Clan.class, ClanPart.CLAN_CONTEXT_KEY),
                clanMember
        );
    }

    @Command(names = "demote", permission = "clans.demote")
    public void runDemote(
            CommandContext commandContext,
            @Sender Player sender, @Sender User user,
            ClanMember clanMember
    ) {
        clanModerationService.demoteMember(
                sender, user,
                commandContext.getObject(Clan.class, ClanPart.CLAN_CONTEXT_KEY),
                clanMember
        );
    }

    @Command(names = "leave", permission = "clans.leave")
    public void runLeave(@Sender Player sender, @Sender User user) {
        clanDisbandService.leaveClan(sender, user);
    }

    @Command(names = "invite", permission = "clans.invite")
    public void runInvite(
            @Sender Player sender, @Sender User user,
            ClusteredUser target
    ) {
        recruitmentService.sendRecruitment(sender, user, target);
    }

    @Command(names = "accept", permission = "clans.accept")
    public void runAccept(
            @Sender Player sender, @Sender User user,
            Clan clan
    ) {
        recruitmentService.acceptRecruitment(sender, user, clan);
    }

    @Command(names = "deny", permission = "clans.deny")
    public void runDeny(@Sender Player sender, Clan clan) {
        recruitmentService.denyRecruitment(sender, clan);
    }

}
