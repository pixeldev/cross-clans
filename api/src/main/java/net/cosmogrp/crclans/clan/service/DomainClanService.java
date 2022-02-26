package net.cosmogrp.crclans.clan.service;

import net.cosmogrp.crclans.domain.Domain;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bukkit.entity.Player;

public interface DomainClanService<T extends Model & DocumentCodec & Domain>
        extends ClanService<T> {

    void sendList(Player player, User user);

}
