package net.cosmogrp.crclans.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.enemy.ClanEnemyService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = "enemy")
public class ClanEnemyCommand implements CommandClass {

    @Inject private ClanEnemyService enemyService;
    @Inject private MessageHandler messageHandler;

    @Command(names = {"", "help"}, permission = "clans.help")
    public void runHelp(@Sender Player sender) {
        messageHandler.send(sender, "commands.enemy-help");
    }

    @Command(names = "list", permission = "clans.enemy.list")
    public void runList(@Sender Player sender, @Sender User user) {
        enemyService.sendList(sender, user);
    }

    @Command(names = "add", permission = "clans.enemy.add")
    public void runAdd(
            @Sender Player sender, @Sender User user,
            ClanMemberData memberData
    ) {
        enemyService.addEnemy(sender, user, memberData);
    }

    @Command(names = "remove", permission = "clans.enemy.remove")
    public void runRemove(
            @Sender Player sender, @Sender User user,
            ClanMemberData memberData
    ) {
        enemyService.removeEnemy(sender, user, memberData);
    }

}
