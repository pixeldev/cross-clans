package net.cosmogrp.crclans.command.internal;

import me.fixeddev.commandflow.annotated.part.AbstractModule;
import me.fixeddev.commandflow.annotated.part.Key;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
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

    @Override
    public void configure() {
        bindFactory(ClusteredUser.class, clusteredUserPart);
        bindFactory(User.class, userPart);
        bindFactory(new Key(User.class, Sender.class), userSenderPart);
    }
}
