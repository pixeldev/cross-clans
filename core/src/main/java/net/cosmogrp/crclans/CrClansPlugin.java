package net.cosmogrp.crclans;

import me.yushust.inject.Injector;
import net.cosmogrp.crclans.inject.MainModule;
import org.bukkit.plugin.java.JavaPlugin;

public class CrClansPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        Injector.create(new MainModule(this))
                .injectMembers(this);
    }

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable() {

    }

}
