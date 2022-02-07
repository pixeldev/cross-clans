package net.cosmogrp.crclans.inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;

import javax.inject.Singleton;

public class SerializationModule extends AbstractModule {

    @Singleton @Provides
    public Gson createGson() {
        return new GsonBuilder().create();
    }

}
