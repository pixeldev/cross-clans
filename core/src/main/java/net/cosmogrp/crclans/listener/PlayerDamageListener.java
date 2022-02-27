package net.cosmogrp.crclans.listener;

import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.clan.data.ClanData;
import net.cosmogrp.crclans.clan.data.ClanDataService;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.inject.Inject;

public class PlayerDamageListener implements Listener {

    @Inject private UserService userService;
    @Inject private ClanDataService dataService;
    @Inject private ClanAllyService allyService;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity source = event.getDamager();

        if (!(target instanceof Player targetPlayer) ||
                !(source instanceof Player sourcePlayer)) {
            return;
        }

        User targetUser = userService.getUser(targetPlayer);
        User sourceUser = userService.getUser(sourcePlayer);

        if (targetUser == null || sourceUser == null) {
            return;
        }

        String targetClan = targetUser.getClanTag();
        String sourceClan = sourceUser.getClanTag();

        if (targetClan == null || sourceClan == null) {
            return;
        }

        ClanData sourceClanData = dataService
                .getData(sourcePlayer, sourceClan);

        if (sourceClanData == null) {
            // this mustn't happen
            return;
        }

        if (sourceClanData.isFriendlyFire()) {
            return;
        }

        if (sourceClan.equals(targetClan)) {
            event.setCancelled(true);
            return;
        }

        ClanAllyData sourceAllyData = allyService
                .getData(sourcePlayer, sourceClan);

        if (sourceAllyData != null) {
            if (sourceAllyData.contains(targetClan)) {
                event.setCancelled(true);
            }
        }
    }

}