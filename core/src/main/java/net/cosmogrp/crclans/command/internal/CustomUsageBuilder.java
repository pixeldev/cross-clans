package net.cosmogrp.crclans.command.internal;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.usage.DefaultUsageBuilder;
import me.yushust.message.MessageHandler;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class CustomUsageBuilder extends DefaultUsageBuilder {

    @Inject private MessageHandler messageHandler;

    @Override
    public Component getUsage(CommandContext commandContext) {
        Component component = super.getUsage(commandContext);
        CommandSender commandSender = commandContext.getObject(
                CommandSender.class,
                BukkitCommandManager.SENDER_NAMESPACE
        );

        return TextComponent.of(messageHandler.get(commandSender, "commands.usage"))
                .append(component.color(TextColor.RED));
    }
}
