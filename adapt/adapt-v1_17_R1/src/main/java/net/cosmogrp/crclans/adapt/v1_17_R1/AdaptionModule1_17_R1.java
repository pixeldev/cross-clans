package net.cosmogrp.crclans.adapt.v1_17_R1;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.message.send.MessageSender;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Singleton;

public class AdaptionModule1_17_R1 extends AbstractModule {

    @Provides
    @Singleton
    public MessageSender<CommandSender> createMessageSender() {
        return (sender, mode, message) -> {
            switch (mode) {
                case "minimessage" -> sender.sendMessage(
                        MiniMessage.miniMessage()
                                .deserialize(message)
                );
                case "notify" -> {
                    sender.sendMessage(message);
                    sender.playSound(Sound.sound(
                            org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING,
                            Sound.Source.NEUTRAL,
                            1, 1
                    ));
                }
                default -> sender.sendMessage(message);
            }
        };
    }

}
