package net.cosmogrp.crclans;

import net.cosmogrp.crclans.clan.channel.ClanChannelRegistry;
import net.cosmogrp.crclans.clan.service.ClanServiceRegistry;
import net.cosmogrp.crclans.user.UserService;

public interface ClansAPI extends ClanServiceRegistry {

    UserService getUserService();

    ClanChannelRegistry getChannelRegistry();

}
