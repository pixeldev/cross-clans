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
        if (notify0(targets, path, parameters)) {
            // if all the targets have been notified
            // we don't need to notify the global channel
            return;
        }

        channel.sendMessage(GlobalMessage.targets(targets, path, parameters));
    }
}