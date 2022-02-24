package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentData;
import net.cosmogrp.crclans.clan.recruitment.ClanRecruitmentService;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class ClanRecruitmentPart implements PartFactory {

    @Inject private ClanRecruitmentService recruitmentService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list) {
        return new ArgumentPart() {
            @Override
            public List<ClanRecruitmentData> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                ClanRecruitmentData recruitmentData =
                        recruitmentService.getData(argumentStack.next());

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
