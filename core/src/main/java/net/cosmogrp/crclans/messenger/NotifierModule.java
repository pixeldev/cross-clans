package net.cosmogrp.crclans.messenger;

import me.yushust.inject.AbstractModule;
import me.yushust.inject.Provides;
import net.cosmogrp.crclans.messenger.global.GlobalMessage;
import net.cosmogrp.crclans.messenger.global.GlobalNotifier;
import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.connection.Redis;

import javax.inject.Singleton;

public class NotifierModule extends AbstractModule {

    @Override
    public void configure() {
        bind(LocalNotifier.class).singleton();
        bind(GlobalNotifier.class).singleton();
    }

    @Provides @Singleton
    public Channel<GlobalMessage> createChannel(Redis redis, LocalNotifier localNotifier) {
        return redis.getMessenger()
                .getChannel("notifier", GlobalMessage.class)
                .addListener(new GlobalMessageListener(localNotifier));
    }

}
