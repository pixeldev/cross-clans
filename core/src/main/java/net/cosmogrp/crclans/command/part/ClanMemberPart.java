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
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.cosmogrp.crclans.command.part.ClanPart.CLAN_CONTEXT_KEY;

public class ClanMemberPart implements PartFactory {

    private static final String USER_CONTEXT_KEY = "user";
    private static final String MEMBER_CONTEXT_KEY = "members";

    @Inject private UserService userService;
    @Inject private ClanUserService clanUserService;

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
                    throw new ArgumentParseException("%translatable:command.not-in-clan");
                }

                Clan clan = commandContext.getObject(Clan.class, CLAN_CONTEXT_KEY);

                if (clan == null) {
                    throw new ArgumentParseException("%translatable:clan.self-not-found%");
                }

                MemberContext memberContext = commandContext.getObject(
                        MemberContext.class,
                        MEMBER_CONTEXT_KEY
                );

                if (memberContext == null) {
                    throw new ArgumentParseException("%translatable:command.error%");
                }

                ClanMember clanMember = memberContext.getMemberId(next);

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

                User user = commandContext.getObject(User.class, USER_CONTEXT_KEY);

                if (user == null) {
                    user = userService.getUser(player);
                    commandContext.setObject(User.class, USER_CONTEXT_KEY, user);
                }

                if (user == null) {
                    return Collections.emptyList();
                }

                String clanTag = user.getClanTag();

                if (clanTag == null) {
                    return Collections.emptyList();
                }

                Clan clan = commandContext.getObject(Clan.class, CLAN_CONTEXT_KEY);

                if (clan == null) {
                    clan = clanUserService.getClan(player, user);

                    if (clan == null) {
                        return Collections.emptyList();
                    }

                    commandContext.setObject(Clan.class, CLAN_CONTEXT_KEY, clan);
                }

                String next = stack.hasNext() ? stack.next() : "";
                MemberContext memberContext = commandContext.getObject(
                        MemberContext.class,
                        MEMBER_CONTEXT_KEY
                );

                if (memberContext == null) {
                    memberContext = new MemberContext();
                    commandContext.setObject(
                            MemberContext.class, MEMBER_CONTEXT_KEY,
                            memberContext
                    );
                }

                List<String> suggestions = new ArrayList<>();
                for (ClanMember member : clan.getMembers()) {
                    String name = member.getPlayerName();
                    if (name.startsWith(next)) {
                        suggestions.add(name);
                        memberContext.addMember(member);
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

    private static class MemberContext {

        private final Map<String, ClanMember> membersByName;

        public MemberContext() {
            this.membersByName = new HashMap<>();
        }

        public void addMember(ClanMember member) {
            membersByName.put(member.getPlayerName(), member);
        }

        public @Nullable ClanMember getMemberId(String name) {
            return membersByName.get(name);
        }
    }
}
