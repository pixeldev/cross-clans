package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.Model;

import java.util.Collection;

public interface ClanServiceRegistry {

    Collection<ClanService<? extends Model>> getServices();

    <T extends Model> ClanService<T> getService(Class<T> modelClass);

    <T extends Model> void registerService(
            Class<T> clazz,
            Class<? extends ClanService<T>> service
    );

    <T extends Model> void unregisterService(Class<T> modelClass);

}
