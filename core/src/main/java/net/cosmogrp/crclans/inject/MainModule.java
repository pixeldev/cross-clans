package net.cosmogrp.crclans.inject;

import me.yushust.inject.AbstractModule;
import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.ClanModule;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.notifier.NotifierModule;
import net.cosmogrp.crclans.server.ServerData;
import net.cosmogrp.crclans.translate.TranslationModule;
import net.cosmogrp.crclans.user.UserModule;
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

        bind(ServerData.class).toInstance(new ServerData(configuration));
        bind(LogHandler.class).toInstance(new LogHandler(plugin.getLogger()));

        install(
                new RedisModule(), new DatabaseModule(),
                new SerializationModule(), new NotifierModule(),
                new TranslationModule(), new ListenerModule(),
                new ClanModule(), new UserModule(), new EconomyModule()
        );
    }

}
