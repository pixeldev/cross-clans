package net.cosmogrp.crclans;

import com.mongodb.client.MongoClient;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.builder.AnnotatedCommandBuilder;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.translator.DefaultTranslator;
import me.yushust.inject.Injector;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.clan.service.ClanServiceRegistry;
import net.cosmogrp.crclans.clan.service.SimpleClanServiceRegistry;
import net.cosmogrp.crclans.command.ClanCommand;
import net.cosmogrp.crclans.command.internal.ClanPartModule;
import net.cosmogrp.crclans.command.internal.CustomTranslationProvider;
import net.cosmogrp.crclans.command.internal.CustomUsageBuilder;
import net.cosmogrp.crclans.inject.MainModule;
import net.cosmogrp.crclans.loader.Loader;
import net.cosmogrp.crclans.placeholder.ClanPlaceholderProvider;
import net.cosmogrp.crclans.user.PlayerService;
import net.cosmogrp.crclans.vault.VaultEconomyHandler;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.redis.connection.Redis;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class CrClansPlugin extends JavaPlugin
        implements ClanServiceRegistry {

    private final Injector injector;
    private final ClanServiceRegistry clanServiceRegistry;

    {
        injector = Injector.create(new MainModule(this));
        clanServiceRegistry = new SimpleClanServiceRegistry(injector);
    }

    @Inject private Set<Listener> listeners;
    @Inject private Set<Loader> loaders;

    @Inject private ClanPartModule clanPartModule;
    @Inject private CustomTranslationProvider translatorProvider;
    @Inject private CustomUsageBuilder usageBuilder;

    @Inject private ClanCommand clanCommand;
    @Inject private VaultEconomyHandler vaultEconomyHandler;
    @Inject private MongoClient mongoClient;
    @Inject private Redis redis;

    @Inject private ClanPlaceholderProvider placeholderProvider;
    @Inject private PlayerService playerService;

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        injector.injectMembers(this);
        loaders.forEach(Loader::load);
    }

    @Override
    public void onEnable() {
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerOutgoingPluginChannel(this, "BungeeCord");

        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        placeholderProvider.register();

        CommandManager commandManager = new BukkitCommandManager("crclans");
        commandManager.setTranslator(new DefaultTranslator(translatorProvider));
        commandManager.setUsageBuilder(usageBuilder);

        PartInjector partInjector = PartInjector.create();
        partInjector.install(new DefaultsModule());
        partInjector.install(new BukkitModule());
        partInjector.install(clanPartModule);

        AnnotatedCommandTreeBuilder builder =
                new AnnotatedCommandTreeBuilderImpl(
                        AnnotatedCommandBuilder.create(partInjector),
                        (clazz, parent) -> injector.getInstance(clazz)
                );

        commandManager.registerCommands(builder.fromClass(clanCommand));

        Bukkit.getScheduler().runTaskLater(this, () -> vaultEconomyHandler.setup(), 1);
    }

    @Override
    public void onDisable() {
        playerService.saveAll();
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        mongoClient.close();

        try {
            redis.close();
        } catch (IOException e) {
            getLogger().severe("Failed to close redis connection");
        }
    }

    public static CrClansPlugin getInstance() {
        return JavaPlugin.getPlugin(CrClansPlugin.class);
    }

    @Override
    public Collection<ClanService<? extends Model>> getServices() {
        return clanServiceRegistry.getServices();
    }

    @Override
    public <T extends Model & DocumentCodec> ClanService<T> getService(Class<T> modelClass) {
        return clanServiceRegistry.getService(modelClass);
    }

    @Override
    public <T extends Model & DocumentCodec> void registerService(
            Class<T> clazz,
            Class<? extends ClanService<T>> service
    ) {
        clanServiceRegistry.registerService(clazz, service);
    }

    @Override
    public <T extends Model & DocumentCodec> void unregisterService(Class<T> modelClass) {
        clanServiceRegistry.unregisterService(modelClass);
    }
}
