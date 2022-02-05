package net.cosmogrp.crclans.inject;

import me.yushust.inject.AbstractModule;
import net.cosmogrp.crclans.listener.PlayerConnectionListener;
import org.bukkit.event.Listener;

public class ListenerModule extends AbstractModule {

    @Override
    public void configure() {
        multibind(Listener.class)
                .asSet()
                .to(PlayerConnectionListener.class);
    }

}
