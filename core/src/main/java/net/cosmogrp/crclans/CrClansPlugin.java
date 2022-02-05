package net.cosmogrp.crclans;

import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.brigadier.BrigadierCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.translator.DefaultTranslator;
import me.yushust.inject.Injector;
import net.cosmogrp.crclans.command.internal.ClanPartModule;
import net.cosmogrp.crclans.command.internal.CustomTranslatorProvider;
import net.cosmogrp.crclans.command.internal.CustomUsageBuilder;
import net.cosmogrp.crclans.inject.MainModule;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.Set;

public class CrClansPlugin extends JavaPlugin {

    @Inject private Set<Listener> listeners;
    @Inject private ClanPartModule clanPartModule;
    @Inject private CustomTranslatorProvider translatorProvider;
    @Inject private CustomUsageBuilder usageBuilder;

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

        CommandManager commandManager = new BrigadierCommandManager(this);
        commandManager.setTranslator(new DefaultTranslator(translatorProvider));
        commandManager.setUsageBuilder(usageBuilder);

        PartInjector partInjector = PartInjector.create();
        partInjector.install(new DefaultsModule());
        partInjector.install(new BukkitModule());
        partInjector.install(clanPartModule);
    }

    @Override
    public void onDisable() {

    }

}
