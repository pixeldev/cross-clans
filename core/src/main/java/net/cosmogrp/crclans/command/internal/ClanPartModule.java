package net.cosmogrp.crclans.command.internal;

import me.fixeddev.commandflow.annotated.part.AbstractModule;
import me.fixeddev.commandflow.annotated.part.Key;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestService;
import net.cosmogrp.crclans.clan.channel.ClanChannel;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentData;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;
import net.cosmogrp.crclans.command.part.ClanAllyDataPartFactory;
import net.cosmogrp.crclans.command.part.ClanChannelPart;
import net.cosmogrp.crclans.command.part.ClanMemberPart;
import net.cosmogrp.crclans.command.part.ClanServicePartFactory;
import net.cosmogrp.crclans.command.part.ClusteredUserPart;
import net.cosmogrp.crclans.command.part.UserPart;
import net.cosmogrp.crclans.command.part.UserSenderPart;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.crclans.user.User;

import javax.inject.Inject;

public class ClanPartModule extends AbstractModule {

    @Inject private ClusteredUserPart clusteredUserPart;
    @Inject private UserPart userPart;
    @Inject private UserSenderPart userSenderPart;
    @Inject private ClanChannelPart clanChannelPart;
    @Inject private ClanMemberPart clanMemberPart;
    @Inject private ClanAllyDataPartFactory allyDataPart;

    @Inject private ClanRecruitmentService recruitmentService;
    @Inject private ClanAllyRequestService allyRequestService;
    @Inject private ClanMemberService memberService;

    @Override
    public void configure() {
        bindFactory(ClusteredUser.class, clusteredUserPart);
        bindFactory(User.class, userPart);
        bindFactory(ClanAllyData.class, allyDataPart);
        bindFactory(ClanMemberData.class, new ClanServicePartFactory<>(memberService));
        bindFactory(ClanRecruitmentData.class, new ClanServicePartFactory<>(recruitmentService));
        bindFactory(ClanAllyRequestData.class, new ClanServicePartFactory<>(allyRequestService));
        bindFactory(ClanMember.class, clanMemberPart);
        bindFactory(ClanChannel.class, clanChannelPart);
        bindFactory(new Key(User.class, Sender.class), userSenderPart);
    }
}
