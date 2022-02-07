package net.cosmogrp.crclans;

import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.brigadier.BrigadierCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.translator.DefaultTranslator;
import me.yushust.inject.Injector;
import net.cosmogrp.crclans.command.ClanCommand;
import net.cosmogrp.crclans.command.internal.ClanPartModule;
import net.cosmogrp.crclans.command.internal.CustomTranslatorProvider;
import net.cosmogrp.crclans.command.internal.CustomUsageBuilder;
import net.cosmogrp.crclans.inject.MainModule;
import net.cosmogrp.crclans.server.ServerNameListener;
import net.cosmogrp.crclans.vault.VaultEconomyHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import javax.inject.Inject;
import java.util.Set;

public class CrClansPlugin extends JavaPlugin {

    @Inject private Set<Listener> listeners;
    @Inject private ServerNameListener serverNameListener;

    @Inject private ClanPartModule clanPartModule;
    @Inject private CustomTranslatorProvider translatorProvider;
    @Inject private CustomUsageBuilder usageBuilder;

    @Inject private ClanCommand clanCommand;
    @Inject private VaultEconomyHandler vaultEconomyHandler;

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Injector.create(new MainModule(this))
                .injectMembers(this);
    }

    @Override
    public void onEnable(){
        Messenger messenger = getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        messenger.registerIncomingPluginChannel(
                this, "BungeeCord",
                serverNameListener
        );

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

        AnnotatedCommandTreeBuilder builder =
                new AnnotatedCommandTreeBuilderImpl(partInjector);

        commandManager.registerCommands(builder.fromClass(clanCommand));

        vaultEconomyHandler.setup();
    }

    @Override
    public void onDisable() {
        Messenger messenger = getServer().getMessenger();
        messenger.unregisterOutgoingPluginChannel(this, "BungeeCord");
        messenger.unregisterIncomingPluginChannel(
                this, "BungeeCord",
                serverNameListener
        );
    }

}
