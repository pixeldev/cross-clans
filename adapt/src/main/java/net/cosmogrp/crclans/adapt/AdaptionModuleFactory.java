package net.cosmogrp.crclans.adapt;

import me.yushust.inject.Module;
import org.bukkit.Bukkit;

public class AdaptionModuleFactory {

    public static final String SERVER_VERSION = Bukkit.getServer()
            .getClass().getPackage()
            .getName().split("\\.")[3]
            .substring(1);

    private static final String CLASS_NAME = AdaptionModuleFactory.class.getPackage().getName()
            + ".v" + SERVER_VERSION + ".AdaptionModule" + SERVER_VERSION;

    private AdaptionModuleFactory() {
        throw new UnsupportedOperationException();
    }

    public static Module create() {
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            Object module = clazz.newInstance();
            if (!(module instanceof Module)) {
                throw new IllegalStateException("Invalid adaption module: '"
                        + CLASS_NAME + "'. It doesn't implement " + Module.class);
            }
            return (Module) module;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Adaption module not found: '" + CLASS_NAME + '.');
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate adaption module", e);
        }
    }


}
