package net.cosmogrp.crclans.inject;

import net.cosmogrp.crclans.vault.VaultEconomyHandler;

public class EconomyModule extends net.cosmogrp.economy.trew.EconomyModule {

    @Override
    protected void configure() {
        bind(VaultEconomyHandler.class).singleton();
    }

}
