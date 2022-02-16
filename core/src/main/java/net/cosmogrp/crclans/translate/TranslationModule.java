package net.cosmogrp.crclans.translate;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.message.MessageHandler;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.send.MessageSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.inject.Singleton;

public class TranslationModule extends AbstractModule {

    @Provides
    @Singleton
    public MessageHandler getMessageHandler(
            Plugin plugin,
            MessageSender<CommandSender> messageSender
    ) {
        return MessageHandler.of(
                BukkitMessageAdapt.newYamlSource(plugin),
                configurationHandle -> {
                    configurationHandle.addInterceptor(message ->
                            ChatColor.translateAlternateColorCodes('&', message));

                    configurationHandle.specify(CommandSender.class)
                            .setLinguist(sender -> "es")
                            .setMessageSender(messageSender);
                }
        );
    }

}
