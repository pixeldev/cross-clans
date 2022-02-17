package net.cosmogrp.crclans.command;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.command.part.ClanPart;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = {"clan", "clans"})
public class ClanCommand implements CommandClass {

    @Inject private ClanService clanService;
    @Inject private ClanUserService clanUserService;
    @Inject private ClanRecruitmentService recruitmentService;

    @Command(names = "create", permission = "clans.create")
    public void create(@Sender Player sender, @Sender User user, String tag) {
        clanService.createClan(user, sender, tag);
    }

    @Command(names = "delete", permission = "clans.delete")
    public void runDelete(@Sender Player sender, @Sender User user) {
        clanUserService.disbandClan(sender, user);
    }

    @Command(names = "kick", permission = "clans.kick")
    public void runKick(
            CommandContext commandContext,
            @Sender Player sender, @Sender User user,
            ClanMember clanMember
    ) {
        clanUserService.kickMember(
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
        clanUserService.promoteMember(
                sender, user,
                commandContext.getObject(Clan.class, ClanPart.CLAN_CONTEXT_KEY),
                clanMember
        );
    }

    @Command(names = "leave", permission = "clans.leave")
    public void runLeave(@Sender Player sender, @Sender User user) {
        clanUserService.leaveClan(sender, user);
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
