package net.cosmogrp.crclans.inject;

import me.yushust.inject.scope.Scopes;
import net.cosmogrp.crclans.vault.VaultEconomyHandler;
import net.cosmogrp.economy.EconomyHandler;

public class EconomyModule extends net.cosmogrp.economy.trew.EconomyModule {

    @Override
    protected void configure() {
        bind(EconomyHandler.class)
                .named("vault")
                .to(VaultEconomyHandler.class)
                .in(Scopes.SINGLETON);
    }

}
