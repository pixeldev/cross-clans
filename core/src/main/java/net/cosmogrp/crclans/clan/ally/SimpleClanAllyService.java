package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.AbstractClanService;

public class SimpleClanAllyService
        extends AbstractClanService<ClanAllyData>
        implements ClanAllyService {

    public SimpleClanAllyService() {
        super(ClanAllyData::create);
    }

}
