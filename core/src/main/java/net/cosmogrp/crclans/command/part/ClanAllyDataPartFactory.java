package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.part.CommandPart;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.user.UserService;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.List;

public class ClanAllyDataPartFactory implements PartFactory {

    @Inject private ClanAllyService allyService;
    @Inject private UserService userService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list
    ) {
        return new ClanAllyDataPart(name, allyService, userService);
    }
}
