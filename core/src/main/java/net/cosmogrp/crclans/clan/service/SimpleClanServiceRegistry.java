package net.cosmogrp.crclans.clan.service;

import me.yushust.inject.Injector;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.clan.service.ClanServiceRegistry;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleClanServiceRegistry
        implements ClanServiceRegistry {

    private final Map<Class<? extends Model>,
            ClanService<? extends Model>> services;

    private final Injector injector;

    public SimpleClanServiceRegistry(Injector injector) {
        this.injector = injector;
        this.services = new HashMap<>();
    }

    @Override
    public Collection<ClanService<? extends Model>> getServices() {
        return services.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model & DocumentCodec> ClanService<T> getService(Class<T> modelClass) {
        return (ClanService<T>) services.get(modelClass);
    }

    @Override
    public <T extends Model & DocumentCodec> void registerService(
            Class<T> clazz,
            Class<? extends ClanService<T>> serviceClass
    ) {
        services.put(clazz, injector.getInstance(serviceClass));
    }

    @Override
    public <T extends Model & DocumentCodec> void unregisterService(Class<T> modelClass) {
        services.remove(modelClass);
    }
}
