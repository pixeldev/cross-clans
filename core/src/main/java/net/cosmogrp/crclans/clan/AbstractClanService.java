package net.cosmogrp.crclans.clan;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.model.Model;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public abstract class AbstractClanService<T extends Model>
        implements ClanService<T> {

    @Inject protected MessageHandler messageHandler;
    @Inject protected LogHandler logHandler;
    @Inject protected CachedRemoteModelService<T> modelService;

    @Override
    public @Nullable T getData(String tag) {
        return modelService.getSync(tag);
    }

    @Override
    public void save(Player player, T data) {
        modelService.save(data)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        logHandler.reportError(
                                "Failed to save clan '%s's data",
                                error, data.getId()
                        );

                        messageHandler.send(player, "clan.save-failed");
                    }
                });
    }

    @Override
    public void save(T data) {
        modelService.save(data)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        logHandler.reportError(
                                "Failed to save clan '%s'",
                                error, data.getId()
                        );
                    }
                });
    }
}
