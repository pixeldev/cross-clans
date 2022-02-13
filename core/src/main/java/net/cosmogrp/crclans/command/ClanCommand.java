package net.cosmogrp.crclans.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import net.cosmogrp.crclans.clan.ClanService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = {"clan", "clans"})
public class ClanCommand implements CommandClass {

    @Inject private ClanService clanService;

    @Command(names = "create", permission = "clans.create")
    public void create(@Sender Player sender, @Sender User user, String tag) {
        clanService.createClan(user, sender, tag);
    }

    @Command(names = "delete", permission = "clans.delete")
    public void runDelete(@Sender Player sender, @Sender User user) {
        clanService.deleteClan(user, sender);
    }

}
