package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanService;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class ClanPart implements PartFactory {


    public static final String CLAN_CONTEXT_KEY = "clan";

    @Inject private ClanService clanService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list) {
        return new ArgumentPart() {
            @Override
            public List<Clan> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                Clan clan = clanService.getClan(argumentStack.next());

                if (clan == null) {
                    throw new ArgumentParseException("%translatable:clan.not-found%");
                }

                return Collections.singletonList(clan);
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
