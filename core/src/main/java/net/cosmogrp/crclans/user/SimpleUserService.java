package net.cosmogrp.crclans.user;

import me.yushust.message.MessageHandler;
import net.cosmogrp.storage.AsyncModelService;
import net.cosmogrp.storage.redis.RedisModelService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class SimpleUserService implements UserService {

    @Inject private AsyncModelService<User> modelService;
    @Inject private RedisModelService<User> redisModelService;
    @Inject private MessageHandler messageHandler;
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
    public void loadOrCreate(Player player) {
        UUID playerId = player.getUniqueId();
        redisModelService.delete(playerId.toString())
                .thenApply(user -> {
                    if (user == null) {
                        return modelService.findSync(playerId.toString());
                    }

                    return user;
                })
                .whenComplete((user, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        Bukkit.getScheduler().runTask(plugin, () ->
                                player.kickPlayer(messageHandler
                                        .getMessage("user.load-error")));
                    }

                    if (user == null) {
                        user = User.create(playerId);
                        modelService.saveSync(user);
                    }
                });
    }
}
