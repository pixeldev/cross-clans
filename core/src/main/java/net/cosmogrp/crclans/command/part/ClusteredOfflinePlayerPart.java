package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ClusteredOfflinePlayerPart implements PartFactory {

    @Inject private RedisCache redisCache;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> annotations
    ) {
        return new ArgumentPart() {
            @Override
            public List<OfflinePlayer> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                String playerId = redisCache.get("players-by-name", argumentStack.next());

                if (playerId == null) {
                    throw new ArgumentParseException("%translatable:clustered-player-not-found%");
                }

                UUID uuid = UUID.fromString(playerId);
                return Collections.singletonList(Bukkit.getOfflinePlayer(uuid));
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                Set<String> clusteredPlayers =
                        redisCache.getAllKeys("players-by-name");

                String next = stack.hasNext() ? stack.next() : "";
                List<String> suggestions = new ArrayList<>();

                for (String playerName : clusteredPlayers) {
                    if (playerName.startsWith(next)) {
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
