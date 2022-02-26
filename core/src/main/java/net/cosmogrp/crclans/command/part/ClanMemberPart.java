package net.cosmogrp.crclans.command.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.cosmogrp.crclans.clan.member.ClanMember;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
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

    public static final String MEMBER_CONTEXT_KEY = "clan-recruitment";

    @Inject private UserService userService;
    @Inject private ClanMemberService memberService;

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
                    throw new ArgumentParseException("%translatable:clan.not-in-clan%");
                }

                ClanMemberData memberData = memberService.getData(clanTag);

                if (memberData == null) {
                    throw new ArgumentParseException("%translatable:clan.self-not-found%");
                }

                commandContext.setObject(ClanMemberData.class, MEMBER_CONTEXT_KEY, memberData);
                ClanMember clanMember = null;

                for (ClanMember member : memberData.getMembers()) {
                    if (member.getPlayerName().equalsIgnoreCase(next)) {
                        clanMember = member;
                    }
                }

                if (clanMember == null) {
                    throw new ArgumentParseException("%translatable:clan.member-not-found%");
                }

                if (clanMember.getPlayerId().equals(user.getPlayerId())) {
                    throw new ArgumentParseException("%translatable:not-self%");
                }

                return Collections.singletonList(clanMember);
            }

            @Override
            public List<String> getSuggestions(
                    CommandContext commandContext,
                    ArgumentStack stack
            ) {
                User user = PartHelper.getUser(userService, commandContext);

                if (user == null) {
                    return Collections.emptyList();
                }

                String clanTag = user.getClanTag();

                if (clanTag == null) {
                    return Collections.emptyList();
                }

                ClanMemberData memberData = memberService.getData(clanTag);

                if (memberData == null) {
                    return Collections.emptyList();
                }

                String next = stack.hasNext() ?
                        stack.next().toLowerCase(Locale.ROOT) :
                        "";

                List<String> suggestions = new ArrayList<>();
                for (ClanMember member : memberData.getMembers()) {
                    if (user.getPlayerId().equals(member.getPlayerId())) {
                        continue;
                    }

                    String name = member.getPlayerName();

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
