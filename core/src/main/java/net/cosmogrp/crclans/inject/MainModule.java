package net.cosmogrp.crclans.inject;

import me.yushust.inject.AbstractModule;
import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.ClanModule;
import net.cosmogrp.crclans.translate.TranslationModule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainModule extends AbstractModule {

    private final CrClansPlugin plugin;

    public MainModule(CrClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void configure() {
        bind(Plugin.class).toInstance(plugin);

        FileConfiguration configuration = plugin.getConfig();
        bind(FileConfiguration.class).toInstance(configuration);
        bind(Executor.class).toInstance(Executors.newFixedThreadPool(
                configuration.getInt("threads")
        ));

        install(
                new RedisModule(), new DatabaseModule(),
                new TranslationModule(),
                new ClanModule(), new EconomyModule()
        );
    }

}
