package net.cosmogrp.crclans.command.internal;

import me.fixeddev.commandflow.annotated.part.AbstractModule;
import net.cosmogrp.crclans.command.part.ClusteredUserPart;
import net.cosmogrp.crclans.user.ClusteredUser;

import javax.inject.Inject;

public class ClanPartModule extends AbstractModule {

    @Inject private ClusteredUserPart clusteredUserPart;

    @Override
    public void configure() {
        bindFactory(ClusteredUser.class, clusteredUserPart);
    }
}
