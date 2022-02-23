package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.Model;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ClanServiceRegistry {

    Collection<ClanService<? extends Model>> getServices();

    @Nullable ClanService<? extends Model> getService(String name);

    void registerService(
            String name,
            ClanService<? extends Model> service
    );

    void unregisterService(String name);

}
