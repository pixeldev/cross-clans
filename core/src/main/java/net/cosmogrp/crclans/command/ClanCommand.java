package net.cosmogrp.crclans.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import net.cosmogrp.crclans.command.part.Clustered;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Command(names = {"clan", "clans"})
public class ClanCommand implements CommandClass {

    @Command(names = "")
    public void run(@Sender Player sender, @Clustered OfflinePlayer target) {
        sender.sendMessage("success " + target.getName());
    }

}
