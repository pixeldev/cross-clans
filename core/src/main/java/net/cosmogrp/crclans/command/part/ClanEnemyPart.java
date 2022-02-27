package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.enemy.ClanEnemyService;
import net.cosmogrp.crclans.user.UserService;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class ClanEnemyPart implements PartFactory {

    @Inject private UserService userService;
    @Inject private ClanEnemyService enemyService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list
    ) {
        return new ArgumentPart() {
            @Override
            public List<String> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                return Collections.singletonList(argumentStack.next());
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                return PartHelper.getSuggestions(
                        userService,
                        commandContext,
                        enemyService,
                        stack
                );
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
