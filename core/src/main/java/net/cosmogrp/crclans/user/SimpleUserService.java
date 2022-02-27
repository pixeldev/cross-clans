package net.cosmogrp.crclans.user;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SimpleUserService implements UserService {

    @Inject private CachedRemoteModelService<User> modelService;
    @Inject private RedisModelService<User> redisModelService;
    @Inject private MessageHandler messageHandler;
    @Inject private LogHandler logHandler;
    @Inject private Plugin plugin;

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
    public CompletableFuture<User> loadOrCreate(Player player) {
        UUID playerId = player.getUniqueId();
        return redisModelService.delete(playerId.toString())
                .thenCompose(user -> {
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

                    return CompletableFuture.completedFuture(user);
                })
                .whenComplete((user, throwable) -> {
                    if (throwable != null || user == null) {
                        logHandler.reportError(
                                "Failed to load or create user '%s'",
                                throwable, playerId.toString()
                        );

                        Bukkit.getScheduler().runTask(
                                plugin,
                                () -> player.kickPlayer(
                                        messageHandler.get(
                                                player,
                                                "user.load-error"
                                        )
                                ));
                    }
                });
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

        // we need to save it in redis cache, so we don't need to
        // get it from database when we load it again
        redisModelService.saveSync(user);

        modelService.upload(user)
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

    @Override
    public User forcedSave(Player player) {
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

        modelService.uploadSync(user);
        return user;
    }
}
