package net.cosmogrp.crclans.user;

import me.yushust.message.MessageHandler;
import net.cosmogrp.storage.AsyncModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleUserService implements UserService {

    @Inject private AsyncModelService<User> modelService;
    @Inject private RedisModelService<User> redisModelService;
    @Inject private MessageHandler messageHandler;

    @Override
    public @Nullable User getUser(Player player) {
        User user = modelService.getSync(player.getUniqueId().toString());

        if (user == null) {
            messageHandler.send(player, "user.not-found");
        }

        return user;
    }

    @Override
    public @Nullable String loadOrCreate(UUID playerId) {
        User user = redisModelService.deleteSync(playerId.toString());

        if (user == null) {
            user = modelService.findSync(playerId.toString());
        }

        if (user == null) {
            user = User.create(playerId);
            modelService.saveSync(user);
        }

        return messageHandler.getMessage("user.load-error");
    }
}
