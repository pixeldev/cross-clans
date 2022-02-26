package net.cosmogrp.crclans.command;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.SubCommandClasses;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import net.cosmogrp.crclans.clan.ClanDataService;
import net.cosmogrp.crclans.clan.channel.ClanChannel;
import net.cosmogrp.crclans.clan.chat.ClanChatService;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.disband.ClanDisbandService;
import net.cosmogrp.crclans.clan.home.ClanHomeService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentData;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.command.part.ClanMemberPart;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = {"clan", "clans"})
@SubCommandClasses({ClanAllyCommand.class})
public class ClanCommand implements CommandClass {

    @Inject private ClanRecruitmentService recruitmentService;
    @Inject private ClanMemberService memberService;
    @Inject private ClanHomeService homeService;
    @Inject private ClanDisbandService disbandService;
    @Inject private ClanDataService dataService;
    @Inject private ClanChatService clanChatService;

    @Command(names = "create", permission = "clans.create")
    public void create(@Sender Player sender, @Sender User user, String tag) {
        dataService.createClan(user, sender, tag);
    }

    @Command(names = "channel", permission = "clans.channel")
    public void runChannel(
            @Sender Player sender, @Sender User user,
            ClanChannel clanChannel
    ) {
        clanChatService.setChannel(sender, user, clanChannel.getId());
    }

    @Command(names = "changeleader", permission = "clans.changeleader")
    public void runChangeLeader(
            @Sender Player sender, @Sender User user,
            ClanMember target
    ) {
        memberService.transferOwner(sender, user, target);
    }

    @Command(names = "sethome", permission = "clans.sethome")
    public void runSetHome(@Sender Player sender, @Sender User user) {
        homeService.setHome(sender, user);
    }

    @Command(names = "delhome", permission = "clans.delhome")
    public void runDelHome(@Sender Player sender, @Sender User user) {
        homeService.delHome(sender, user);
    }

    @Command(names = "home", permission = "clans.home")
    public void runHome(@Sender Player sender, @Sender User user) {
        homeService.teleportToHome(sender, user);
    }

    @Command(names = "disband", permission = "clans.disband")
    public void runDelete(@Sender Player sender, @Sender User user) {
        disbandService.disbandClan(sender, user);
    }

    @Command(names = "kick", permission = "clans.kick")
    public void runKick(
            CommandContext commandContext,
            @Sender Player sender, @Sender User user,
            ClanMember clanMember
    ) {
        memberService.kick(
                sender, user,
                commandContext.getObject(ClanMemberData.class, ClanMemberPart.MEMBER_CONTEXT_KEY),
                clanMember
        );
    }

    @Command(names = "promote", permission = "clans.promote")
    public void runPromote(
            CommandContext commandContext,
            @Sender Player sender, @Sender User user,
            ClanMember clanMember
    ) {
        memberService.promote(
                sender, user,
                commandContext.getObject(ClanMemberData.class, ClanMemberPart.MEMBER_CONTEXT_KEY),
                clanMember
        );
    }

    @Command(names = "demote", permission = "clans.demote")
    public void runDemote(
            CommandContext commandContext,
            @Sender Player sender, @Sender User user,
            ClanMember clanMember
    ) {
        memberService.demote(
                sender, user,
                commandContext.getObject(ClanMemberData.class, ClanMemberPart.MEMBER_CONTEXT_KEY),
                clanMember
        );
    }

    @Command(names = "leave", permission = "clans.leave")
    public void runLeave(@Sender Player sender, @Sender User user) {
        disbandService.leaveClan(sender, user);
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
            ClanRecruitmentData data
    ) {
        recruitmentService.acceptRecruitment(sender, user, data);
    }

    @Command(names = "deny", permission = "clans.deny")
    public void runDeny(@Sender Player sender, ClanRecruitmentData data) {
        recruitmentService.denyRecruitment(sender, data);
    }

}
