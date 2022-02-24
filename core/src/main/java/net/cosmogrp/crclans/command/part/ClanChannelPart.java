package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.channel.ClanChannel;
import net.cosmogrp.crclans.clan.channel.ClanChannelRegistry;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClanChannelPart implements PartFactory {

    @Inject private ClanChannelRegistry clanChannelRegistry;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list
    ) {
        return new ArgumentPart() {
            @Override
            public List<ClanChannel> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                String next = argumentStack.next().toLowerCase(Locale.ROOT);

                ClanChannel channel = clanChannelRegistry.getChannel(next);

                if (channel == null) {
                    throw new ArgumentParseException("%translatable:clan.channel-not-found%");
                }

                return Collections.singletonList(channel);
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                String next = stack.hasNext() ?
                        stack.next().toLowerCase(Locale.ROOT) :
                        "";
                List<String> suggestions = new ArrayList<>();

                for (String channel : clanChannelRegistry.getChannels()) {
                    if (channel.toLowerCase(Locale.ROOT).startsWith(next)) {
                        suggestions.add(channel);
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
