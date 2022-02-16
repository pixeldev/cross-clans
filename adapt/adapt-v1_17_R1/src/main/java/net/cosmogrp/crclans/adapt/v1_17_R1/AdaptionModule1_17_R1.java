package net.cosmogrp.crclans.adapt.v1_17_R1;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import me.yushust.message.send.MessageSender;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Singleton;

public class AdaptionModule1_17_R1 extends AbstractModule {

    @Provides
    @Singleton
    public MessageSender<CommandSender> createMessageSender() {
        return (sender, mode, message) -> {
            if (mode.equals("minimessage") &&
                    sender instanceof Player) {
                sender.sendMessage(MiniMessage.miniMessage()
                                .deserialize(message));
            } else {
                sender.sendMessage(message);
            }
        };
    }

}
