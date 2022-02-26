package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.user.UserService;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClanAllyDataPart implements PartFactory {

    @Inject private ClanAllyService allyService;
    @Inject private UserService userService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list
    ) {
        return new ArgumentPart() {
            @Override
            public List<ClanAllyData> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                ClanAllyData allyData = allyService
                        .getData(argumentStack.next());

                if (allyData == null) {
                    throw new ArgumentParseException("%translatable:clan.not-found%");
                }

                return Collections.singletonList(allyData);
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                String clanTag = PartHelper.getClanTag(userService, commandContext);

                if (clanTag == null) {
                    return Collections.emptyList();
                }

                ClanAllyData allyData = allyService.getData(clanTag);

                if (allyData == null) {
                    return Collections.emptyList();
                }

                String next = !stack.hasNext() ? "" :
                        stack.next().toLowerCase(Locale.ROOT);
                List<String> suggestions = new ArrayList<>();

                for (String allyTag : allyData.getAllies()) {
                    if (allyTag.toLowerCase(Locale.ROOT).startsWith(next)) {
                        suggestions.add(allyTag);
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
