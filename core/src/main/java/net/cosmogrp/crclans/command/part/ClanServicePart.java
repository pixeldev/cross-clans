package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public record ClanServicePart<T extends Model & DocumentCodec>
        (ClanService<T> clanService)
        implements PartFactory {

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list) {
        return new ArgumentPart() {
            @Override
            public List<T> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                T recruitmentData = clanService
                        .getData(argumentStack.next());

                if (recruitmentData == null) {
                    throw new ArgumentParseException("%translatable:clan.not-found%");
                }

                return Collections.singletonList(recruitmentData);
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
