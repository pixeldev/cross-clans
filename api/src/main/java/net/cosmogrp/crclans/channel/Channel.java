package net.cosmogrp.crclans.channel;

import com.google.gson.reflect.TypeToken;
import net.cosmogrp.crclans.messenger.Messenger;

import java.util.Set;

public interface Channel<T> {

    String getName();

    Messenger getMessenger();

    TypeToken<T> getType();

    Channel<T> sendMessage(T message);

    Channel<T> addListener(ChannelListener<T> channelListener);

    Set<ChannelListener<T>> getListeners();

}
