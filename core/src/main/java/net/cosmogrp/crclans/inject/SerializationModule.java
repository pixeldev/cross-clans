package net.cosmogrp.crclans.inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.domain.Domain;
import net.cosmogrp.crclans.time.TimeStamped;
import net.cosmogrp.storage.gson.PolymorphismTypeAdapter;

import javax.inject.Singleton;

public class SerializationModule extends AbstractModule {

    @Singleton @Provides
    public Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(TimeStamped.class, new PolymorphismTypeAdapter<>())
                .registerTypeAdapter(Domain.class, new PolymorphismTypeAdapter<>())
                .create();
    }

}
