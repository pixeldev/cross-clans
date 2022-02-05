package net.cosmogrp.crclans;

import me.yushust.inject.Injector;
import net.cosmogrp.crclans.inject.MainModule;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.Set;

public class CrClansPlugin extends JavaPlugin {

    @Inject private Set<Listener> listeners;

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Injector.create(new MainModule(this))
                .injectMembers(this);
    }

    @Override
    public void onEnable(){
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {

    }

}
