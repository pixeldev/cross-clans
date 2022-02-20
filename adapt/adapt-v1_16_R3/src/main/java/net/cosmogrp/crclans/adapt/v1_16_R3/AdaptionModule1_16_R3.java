package net.cosmogrp.crclans.adapt.v1_16_R3;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.message.send.MessageSender;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Singleton;

public class AdaptionModule1_16_R3 extends AbstractModule {

    @Provides @Singleton
    public MessageSender<CommandSender> createMessageSender(Plugin plugin) {
        return new MessageSender<>() {

            private final BukkitAudiences audiences =
                    BukkitAudiences.create(plugin);

            @Override
            public void send(CommandSender sender, String mode, String message) {
                switch (mode) {
                    case "minimessage" ->  audiences.player((Player) sender)
                            .sendMessage(MiniMessage.miniMessage()
                                    .deserialize(message));
                    case "notify" -> {
                        sender.sendMessage(message);

                        if (sender instanceof Player player) {
                            player.playSound(
                                    player.getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_PLING,
                                    1, 1
                            );
                        }
                    }
                    default -> sender.sendMessage(message);
                }
            }
        };
    }

}
