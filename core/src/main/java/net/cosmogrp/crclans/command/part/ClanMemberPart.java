package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.Clan;
import net.cosmogrp.crclans.clan.ClanMember;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static net.cosmogrp.crclans.command.part.UserSenderPart.USER_CONTEXT_KEY;

public class ClanMemberPart implements PartFactory {

    @Inject private UserService userService;
    @Inject private ClanService clanService;

    @Override
    public CommandPart createPart(
            String name,
            List<? extends Annotation> list) {
        return new ArgumentPart() {

            @Override
            public List<ClanMember> parseValue(
                    CommandContext commandContext,
                    ArgumentStack argumentStack,
                    CommandPart commandPart
            ) throws ArgumentParseException {
                String next = argumentStack.next();

                User user = commandContext.getObject(User.class, USER_CONTEXT_KEY);

                if (user == null) {
                    throw new ArgumentParseException("%translatable:command.error%");
                }

                String clanTag = user.getClanTag();

                if (clanTag == null) {
                    throw new ArgumentParseException("%translatable:command.not-in-clan%");
                }

                Clan clan = clanService.getClan(clanTag);

                if (clan == null) {
                    throw new ArgumentParseException("%translatable:clan.self-not-found%");
                }

                commandContext.setObject(Clan.class, ClanPart.CLAN_CONTEXT_KEY, clan);
                ClanMember clanMember = null;

                for (ClanMember member : clan.getMembers()) {
                    if (member.getPlayerName().equalsIgnoreCase(next)) {
                        clanMember = member;
                    }
                }

                if (clanMember == null) {
                    throw new ArgumentParseException("%translatable:clan.member-not-found%");
                }

                return Collections.singletonList(clanMember);
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                CommandSender commandSender = commandContext.getObject(
                        CommandSender.class,
                        BukkitCommandManager.SENDER_NAMESPACE
                );

                if (!(commandSender instanceof Player player)) {
                    return Collections.emptyList();
                }

                User user = userService.getUser(player.getUniqueId());

                if (user == null) {
                    return Collections.emptyList();
                }

                String clanTag = user.getClanTag();

                if (clanTag == null) {
                    return Collections.emptyList();
                }

                Clan clan = clanService.getClan(clanTag);

                if (clan == null) {
                    return Collections.emptyList();
                }

                String next = stack.hasNext() ?
                        stack.next().toLowerCase(Locale.ROOT) :
                        "";

                List<String> suggestions = new ArrayList<>();
                for (ClanMember member : clan.getMembers()) {
                    String name = member.getPlayerName();
                    if (commandSender.getName().equals(name)) {
                        continue;
                    }

                    if (name.toLowerCase(Locale.ROOT).startsWith(next)) {
                        suggestions.add(name);
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
