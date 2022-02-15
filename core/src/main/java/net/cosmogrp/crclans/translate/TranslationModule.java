package net.cosmogrp.crclans.translate;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.message.MessageHandler;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Singleton;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class TranslationModule extends AbstractModule {

    @Provides @Singleton
    public BukkitAudiences createAudience(Plugin plugin) {
        return BukkitAudiences.create(plugin);
    }

    @Provides
    @Singleton
    public MessageHandler getMessageHandler(
            Plugin plugin,
            BukkitAudiences audiences
    ) {
        return MessageHandler.of(
                BukkitMessageAdapt.newYamlSource(plugin),
                configurationHandle -> {
                    configurationHandle.specify(CommandSender.class)
                            .setLinguist(sender -> "es")
                            .setMessageSender((sender, mode, message) -> {
                                if (mode.equals("minimessage") &&
                                        sender instanceof Player) {
                                    audiences.player((Player) sender)
                                            .sendMessage(MiniMessage.miniMessage()
                                                    .deserialize(message));
                                } else {
                                    sender.sendMessage(translateAlternateColorCodes(
                                            '&', message
                                    ));
                                }
                            });
                }
        );
    }

}
