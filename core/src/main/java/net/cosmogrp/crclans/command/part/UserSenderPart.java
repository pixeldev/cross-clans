package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.List;

public class UserSenderPart implements PartFactory {

    @Inject private UserService userService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list
    ) {
        return new CommandPart() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void parse(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    @Nullable CommandPart commandPart
            ) throws ArgumentParseException {
                CommandSender sender = commandContext.getObject(
                        CommandSender.class,
                        BukkitCommandManager.SENDER_NAMESPACE
                );

                if (sender != null) {
                    if (sender instanceof Player) {
                        User user = userService.getUser(((Player) sender)
                                .getUniqueId());

                        if (user == null) {
                            throw new ArgumentParseException("%translatable:user.not-found%");
                        }

                        commandContext.setValue(this, user);
                    } else {
                        throw new ArgumentParseException("%translatable:sender.only-player%");
                    }
                } else {
                    throw new ArgumentParseException("%translatable:sender.unknown%");
                }
            }
        };
    }
}
