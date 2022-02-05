package net.cosmogrp.crclans.command.internal;

import me.fixeddev.commandflow.annotated.part.AbstractModule;
import me.fixeddev.commandflow.annotated.part.Key;
import net.cosmogrp.crclans.command.part.Clustered;
import net.cosmogrp.crclans.command.part.ClusteredOfflinePlayerPart;
import org.bukkit.OfflinePlayer;

import javax.inject.Inject;

public class ClanPartModule extends AbstractModule {

    @Inject private ClusteredOfflinePlayerPart offlinePlayerPart;

    @Override
    public void configure() {
        bindFactory(new Key(OfflinePlayer.class, Clustered.class), offlinePlayerPart);
    }
}
