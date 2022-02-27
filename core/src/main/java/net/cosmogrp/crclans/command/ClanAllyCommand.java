package net.cosmogrp.crclans.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestData;
import net.cosmogrp.crclans.clan.ally.ClanAllyRequestService;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = "ally")
public class ClanAllyCommand implements CommandClass {

    @Inject private ClanAllyRequestService allyRequestService;
    @Inject private ClanAllyService allyService;
    @Inject private MessageHandler messageHandler;

    @Command(names = {"", "help"}, permission = "clans.help")
    public void runHelp(@Sender Player sender) {
        messageHandler.send(sender, "commands.ally-help");
    }

    @Command(names = "list", permission = "clans.ally.list")
    public void runList(@Sender Player sender, @Sender User user) {
        allyService.sendList(sender, user);
    }

    @Command(names = "accept", permission = "clans.ally.accept")
    public void runAccept(
            @Sender Player sender, @Sender User user,
            ClanAllyRequestData requestData
    ) {
        allyRequestService.acceptAlly(sender, user, requestData);
    }

    @Command(names = "remove", permission = "clans.ally.remove")
    public void runRemove(
            @Sender Player sender, @Sender User user,
            ClanAllyData allyData
    ) {
        allyService.removeAlly(sender, user, allyData);
    }

    @Command(names = "deny", permission = "clans.ally.deny")
    public void runDeny(
            @Sender Player sender, @Sender User user,
            ClanAllyRequestData requestData
    ) {
        allyRequestService.denyAlly(sender, user, requestData);
    }

    @Command(names = "add", permission = "clans.ally.add")
    public void runAdd(
            @Sender Player sender, @Sender User user,
            String targetTag
    ) {
        allyRequestService.sendAllyRequest(sender, user, targetTag);
    }

}
