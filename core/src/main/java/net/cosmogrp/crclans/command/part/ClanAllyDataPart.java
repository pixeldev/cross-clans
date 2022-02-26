package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
        String clanTag = PartHelper.getClanTag(userService, commandContext);

        if (clanTag == null) {
            return Collections.emptyList();
        }

        ClanAllyData allyData = clanService.getData(clanTag);

        if (allyData == null) {
            return Collections.emptyList();
        }

        String next = !stack.hasNext() ? "" :
                stack.next().toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();

        for (String allyTag : allyData.getAll()) {
            if (allyTag.toLowerCase(Locale.ROOT).startsWith(next)) {
                suggestions.add(allyTag);
            }
        }

        return suggestions;
    }
}
