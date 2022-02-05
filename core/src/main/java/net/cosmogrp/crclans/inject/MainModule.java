package net.cosmogrp.crclans.inject;

import me.yushust.inject.AbstractModule;
import net.cosmogrp.crclans.CrClansPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class MainModule extends AbstractModule {

    private final CrClansPlugin plugin;

    public MainModule(CrClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(FileConfiguration.class).toInstance(plugin.getConfig());

        install(new RedisModule());
    }

}
