package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;

import java.util.Collection;

public interface ClanServiceRegistry {

    Collection<ClanService<? extends Model>> getServices();

    <T extends Model & DocumentCodec> ClanService<T> getService(Class<T> modelClass);

    <T extends Model & DocumentCodec> void registerService(
            Class<T> clazz,
            Class<? extends ClanService<T>> service
    );

    <T extends Model & DocumentCodec> void unregisterService(Class<T> modelClass);

}
