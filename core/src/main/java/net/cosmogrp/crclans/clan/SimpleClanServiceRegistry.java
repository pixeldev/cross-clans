package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.Model;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleClanServiceRegistry
        implements ClanServiceRegistry {

    private final Map<String, ClanService<? extends Model>> services;

    public SimpleClanServiceRegistry() {
        this.services = new HashMap<>();
    }

    @Override
    public Collection<ClanService<? extends Model>> getServices() {
        return services.values();
    }

    @Override
    public @Nullable ClanService<? extends Model> getService(String name) {
        return services.get(name);
    }

    @Override
    public void registerService(
            String name,
            ClanService<? extends Model> service
    ) {
        services.put(name, service);
    }

    @Override
    public void unregisterService(String name) {
        services.remove(name);
    }
}
