package net.cosmogrp.crclans.user;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleUserService implements UserService {

    @Inject private CachedRemoteModelService<User> modelService;
    @Inject private RedisModelService<User> redisModelService;
    @Inject private MessageHandler messageHandler;
    @Inject private LogHandler logHandler;

    @Override
    public @Nullable User getUser(Player player) {
        User user = modelService.getSync(player.getUniqueId().toString());

        if (user == null) {
            messageHandler.send(player, "user.not-found");
        }

        return user;
    }

    @Override
    public @Nullable User getUser(UUID playerId) {
        return modelService.getSync(playerId.toString());
    }

    @Override
    public @Nullable String loadOrCreate(UUID playerId) {
        try {
            User user = redisModelService.deleteSync(playerId.toString());

            // user hasn't been cached or it has expired
            if (user == null) {
                // try to get it from database
                user = modelService.findSync(playerId.toString());

                // check again, if it's still null, create a new one
                if (user == null) {
                    user = User.create(playerId);
                    modelService.saveSync(user);
                }
            } else {
                // add to local cache if already exists
                modelService.saveInCache(user);
            }

            return null;
        } catch (Throwable e) {
            logHandler.reportError(
                    "Failed to load or create user '%s'",
                    e, playerId.toString()
            );

            return messageHandler.getMessage("user.load-error");
        }
    }

    @Override
    public User saveUser(Player player) {
        UUID playerId = player.getUniqueId();
        String playerIdString = playerId.toString();
        User user = modelService.getSync(playerIdString);

        if (user == null) {
            logHandler.reportError(
                    "Failed to get user '%s' from cache",
                    playerIdString
            );

            return null;
        }

        modelService.save(user)
                // we need to save it in redis cache, so we don't need to
                // get it from database when we load it again
                .thenCompose(savedUser -> redisModelService.save(user))
                .whenComplete((savedUser, throwable) -> {
                    if (throwable != null) {
                        // just report the error, don't care if it fails
                        logHandler.reportError(
                                "Failed to save user '%s'",
                                throwable, playerIdString
                        );
                    }
                });

        return user;
    }
}
