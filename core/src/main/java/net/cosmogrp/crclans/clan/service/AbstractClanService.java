package net.cosmogrp.crclans.clan.service;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.storage.dist.CachedRemoteModelService;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.function.Function;

public abstract class AbstractClanService<T extends Model & DocumentCodec>
        implements ClanService<T> {

    @Inject protected MessageHandler messageHandler;
    @Inject protected LogHandler logHandler;
    @Inject protected CachedRemoteModelService<T> modelService;

    private final Function<String, T> creator;

    public AbstractClanService(Function<String, T> creator) {
        this.creator = creator;
    }

    @Override
    public @Nullable T getData(Player player, String tag) {
        try {
            T data = modelService.getSync(tag);

            if (data == null) {
                messageHandler.send(player, "clan.not-found");
            }

            return data;
        } catch (Exception e) {
            logHandler.reportError(
                    "Failed to get clan '%s'",
                    e, tag
            );
            messageHandler.send(player, "clan.error-finding-data");
            return null;
        }
    }

    @Override
    public @Nullable T getData(String tag) {
        try {
            return modelService.getSync(tag);
        } catch (Exception e) {
            logHandler.reportError(
                    "Failed to get clan '%s'",
                    e, tag
            );
            return null;
        }
    }

    @Override
    public void deleteSync(String tag) {
        modelService.deleteSync(tag);
    }

    @Override
    public void createSync(Player creator, String tag) {
        modelService.saveSync(this.creator.apply(tag));
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

    @Override
    public void saveSync(T data) {
        modelService.saveSync(data);
    }
}
