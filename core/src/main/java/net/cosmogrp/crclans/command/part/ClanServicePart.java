package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;

import java.util.Collections;
import java.util.List;

public class ClanServicePart<T extends Model & DocumentCodec>
        implements ArgumentPart {

    private final String name;
    protected final ClanService<T> clanService;

    public ClanServicePart(
            String name,
            ClanService<T> clanService
    ) {
        this.name = name;
        this.clanService = clanService;
    }

    @Override
    public List<T> parseValue(
            CommandContext commandContext,
            ArgumentStack argumentStack,
            CommandPart commandPart
    ) throws ArgumentParseException {
        User user = commandContext.getObject(
                User.class,
                UserSenderPart.USER_CONTEXT_KEY
        );

        if (user == null) {
            throw new ArgumentParseException("%translatable:command.error%");
        }

        T data = clanService
                .getData(argumentStack.next());

        if (data == null) {
            throw new ArgumentParseException("%translatable:clan.not-found%");
        }

        String tag = user.getClanTag();
        if (tag != null && tag.equals(data.getId())) {
            throw new ArgumentParseException("%translatable:clan.not-self%");
        }

        return Collections.singletonList(data);
    }

    @Override
    public String getName() {
        return name;
    }
}
