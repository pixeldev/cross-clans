package net.cosmogrp.crclans.messenger;

import net.cosmogrp.crclans.messenger.global.GlobalMessage;
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
        localNotifier.notify(
                object.getTargets(),
                object.getPath(),
                object.getReplacements()
        );
    }
}
