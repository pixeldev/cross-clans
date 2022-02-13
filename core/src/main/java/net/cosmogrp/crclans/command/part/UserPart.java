package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserPart implements PartFactory {

    @Inject private UserService userService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list
    ) {
        return new ArgumentPart() {
            @Override
            public List<User> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                String name = argumentStack.next();

                Player player = Bukkit.getPlayerExact(name);

                if (player == null) {
                    throw new ArgumentParseException("%translatable:player.offline%");
                }

                User user = userService.getUser(
                        player.getUniqueId()
                );

                if (user == null) {
                    throw new ArgumentParseException("%translatable:user.target-not-found%");
                }

                return Collections.singletonList(user);
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                String last = stack.hasNext() ? stack.next() : null;
                if (last == null) {
                    return Collections.emptyList();
                } else if (Bukkit.getPlayerExact(last) != null) {
                    return Collections.emptyList();
                } else {
                    List<String> names = new ArrayList<>();

                    for (Player player : Bukkit.matchPlayer(last)) {
                        names.add(player.getName());
                    }

                    return names;
                }
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
