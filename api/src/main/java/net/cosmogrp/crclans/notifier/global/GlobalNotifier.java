package net.cosmogrp.crclans.notifier.global;

import net.cosmogrp.crclans.notifier.LocalNotifier;
import net.cosmogrp.storage.redis.channel.Channel;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

public class GlobalNotifier extends LocalNotifier {

    @Inject private Channel<GlobalMessage> channel;

    @Override
    public void notify(Set<UUID> targets, String path, Object... parameters) {
        super.notify(targets, path, parameters);
        channel.sendMessage(GlobalMessage.targets(targets, path, parameters));
    }
}
