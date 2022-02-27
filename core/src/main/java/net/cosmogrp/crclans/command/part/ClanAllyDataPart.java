package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.user.UserService;

import java.util.List;

public class ClanAllyDataPart extends ClanServicePart<ClanAllyData> {

    private final UserService userService;

    public ClanAllyDataPart(
            String name,
            ClanService<ClanAllyData> clanService,
            UserService userService
    ) {
        super(name, clanService);
        this.userService = userService;
    }

    @Override
    public List<String> getSuggestions(
            CommandContext commandContext,
            ArgumentStack stack
    ) {
        return PartHelper.getSuggestions(
                userService,
                commandContext,
                clanService,
                stack
        );
    }
}
