package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.user.cluster.ClusteredUser;
import net.cosmogrp.crclans.user.cluster.ClusteredUserRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClusteredUserPart implements PartFactory {

    @Inject private ClusteredUserRegistry clusteredUserRegistry;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> annotations
    ) {
        return new ArgumentPart() {
            @Override
            public List<ClusteredUser> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                ClusteredUser clusteredUser = clusteredUserRegistry.find(argumentStack.next());

                if (clusteredUser == null) {
                    throw new ArgumentParseException("%translatable:clustered-player-not-found%");
                }

                CommandSender commandSender = commandContext.getObject(
                        CommandSender.class,
                        BukkitCommandManager.SENDER_NAMESPACE
                );

                if (!(commandSender instanceof Player player)) {
                    throw new ArgumentParseException("%translatable:sender.only-player%");
                }

                if (clusteredUser.getPlayerId().equals(player.getUniqueId())) {
                    throw new ArgumentParseException("%translatable:not-self%");
                }

                return Collections.singletonList(clusteredUser);
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                Collection<String> clusteredPlayers =
                        clusteredUserRegistry.getClusteredUsers();
                String next = stack.hasNext() ? stack.next().toLowerCase(Locale.ROOT) : "";
                List<String> suggestions = new ArrayList<>();

                CommandSender commandSender = commandContext.getObject(
                        CommandSender.class,
                        BukkitCommandManager.SENDER_NAMESPACE
                );

                for (String playerName : clusteredPlayers) {
                    if (commandSender.getName().equals(playerName)) {
                        continue;
                    }

                    if (playerName.toLowerCase(Locale.ROOT).startsWith(next)) {
                        suggestions.add(playerName);
                    }
                }

                return suggestions;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
