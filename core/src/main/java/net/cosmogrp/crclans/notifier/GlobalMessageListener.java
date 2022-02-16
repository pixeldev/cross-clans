package net.cosmogrp.crclans.notifier;

import net.cosmogrp.crclans.notifier.global.GlobalMessage;
import net.cosmogrp.storage.redis.channel.Channel;
import net.cosmogrp.storage.redis.channel.ChannelListener;

public class GlobalMessageListener implements ChannelListener<GlobalMessage> {

    private final LocalNotifier localNotifier;

    public GlobalMessageListener(LocalNotifier localNotifier) {
        this.localNotifier = localNotifier;
    }

    @Override
    public void listen(
            Channel<GlobalMessage> channel,
            String server,
            GlobalMessage object
    ) {
        localNotifier.notifyIn(
                object.getTargets(),
                object.getMode(),
                object.getPath(),
                object.getReplacements()
        );
    }
}
