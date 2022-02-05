package net.cosmogrp.crclans.channel;

import com.google.gson.reflect.TypeToken;
import net.cosmogrp.crclans.messenger.Messenger;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Channel<T> {

    String getName();

    Messenger getMessenger();

    TypeToken<T> getType();

    Channel<T> sendMessage(T message, @Nullable String targetServer);

    default Channel<T> sendMessage(T message) {
        return sendMessage(message, null);
    }

    Channel<T> addListener(ChannelListener<T> channelListener);

    void listen(String server, T object);

    Set<ChannelListener<T>> getListeners();

}
